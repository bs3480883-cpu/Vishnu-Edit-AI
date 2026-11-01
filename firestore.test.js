const { test, before, after, beforeEach } = require("node:test");
const assert = require("node:assert");
const fs = require("node:fs");
const crypto = require("node:crypto");
const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");

const PROJECT_ID = "demo-no-project";
const VALID_PW_HASH = crypto
  .createHash("sha256")
  .update("Vishnu@2026", "utf8")
  .digest("hex");
const WRONG_PW_HASH = crypto
  .createHash("sha256")
  .update("WrongPassword123", "utf8")
  .digest("hex");

let testEnv;

function validVideoPayload(sessionId, overrides = {}) {
  const now = new Date();
  return {
    title: "Neo-Tokyo Rain & Holographic Horizon",
    description: "Anamorphic 8K AI cinema piece exploring rain-slicked cyberpunk streets.",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    thumbnailUrl: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200",
    prompt: "Ultra-cinematic wide tracking shot through a futuristic cyberpunk metropolis at night, 8k.",
    category: "Cinematic AI Videos",
    published: true,
    featured: true,
    popularPrompt: true,
    durationSeconds: 15,
    aspectRatio: "16:9",
    aiModel: "Veo 2 Cinema",
    authorId: "vishnu_owner_studio",
    adminSessionId: sessionId,
    pendingDelete: false,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("1. Public visitor (no login) CAN read and list published videos, but CANNOT read unpublished drafts", async () => {
  const sessionId = "owner_sess_1";
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("admin_sessions").doc(sessionId).set({
      sessionId,
      passkeyHash: VALID_PW_HASH,
      createdAt: new Date(),
    });
    await db.collection("videos").doc("pub_vid_1").set(
      validVideoPayload(sessionId, { published: true })
    );
    await db.collection("videos").doc("draft_vid_1").set(
      validVideoPayload(sessionId, { published: false })
    );
  });

  const publicDb = testEnv.unauthenticatedContext().firestore();

  // Public visitor can get published video without any login
  await assertSucceeds(publicDb.collection("videos").doc("pub_vid_1").get());

  // Public visitor can list published videos without any login
  await assertSucceeds(
    publicDb.collection("videos").where("published", "==", true).get()
  );

  // Public visitor CANNOT get unpublished draft video
  await assertFails(publicDb.collection("videos").doc("draft_vid_1").get());
});

test("2. Public visitor (no login) CANNOT create, update, or delete videos", async () => {
  const sessionId = "owner_sess_2";
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("admin_sessions").doc(sessionId).set({
      sessionId,
      passkeyHash: VALID_PW_HASH,
      createdAt: new Date(),
    });
    await db.collection("videos").doc("pub_vid_2").set(
      validVideoPayload(sessionId, { published: true })
    );
  });

  const publicDb = testEnv.unauthenticatedContext().firestore();

  // Visitor without valid adminSessionId cannot create
  await assertFails(
    publicDb
      .collection("videos")
      .doc("hack_vid")
      .set(validVideoPayload("fake_unverified_session"))
  );

  // Visitor without valid adminSessionId cannot update
  await assertFails(
    publicDb
      .collection("videos")
      .doc("pub_vid_2")
      .set(validVideoPayload("fake_unverified_session", { title: "Hacked" }))
  );

  // Visitor cannot delete published video directly (pendingDelete is false)
  await assertFails(publicDb.collection("videos").doc("pub_vid_2").delete());
});

test("3. Admin password verification: wrong password fails on server; valid password creates session; listing sessions is forbidden", async () => {
  const db = testEnv.unauthenticatedContext().firestore();

  // Wrong password hash is rejected by Firestore rules
  await assertFails(
    db.collection("admin_sessions").doc("bad_sess").set({
      sessionId: "bad_sess",
      passkeyHash: WRONG_PW_HASH,
      createdAt: new Date(),
    })
  );

  // Valid password hash is accepted by Firestore rules
  await assertSucceeds(
    db.collection("admin_sessions").doc("valid_sess").set({
      sessionId: "valid_sess",
      passkeyHash: VALID_PW_HASH,
      createdAt: new Date(),
    })
  );

  // Listing /admin_sessions is strictly forbidden so session IDs cannot be enumerated
  await assertFails(db.collection("admin_sessions").get());
});

test("4. Verified Owner Admin session can create, update, unpublish, and delete videos", async () => {
  const db = testEnv.unauthenticatedContext().firestore();
  const sessionId = "owner_verified_sess";

  await assertSucceeds(
    db.collection("admin_sessions").doc(sessionId).set({
      sessionId,
      passkeyHash: VALID_PW_HASH,
      createdAt: new Date(),
    })
  );

  const createdAt = new Date(Date.now() - 5000);
  const basePayload = validVideoPayload(sessionId, { createdAt, updatedAt: createdAt });

  // Create
  await assertSucceeds(db.collection("videos").doc("vid_owner_1").set(basePayload));

  // Update & unpublish
  await assertSucceeds(
    db.collection("videos").doc("vid_owner_1").set({
      ...basePayload,
      title: "Updated Director Cut",
      published: false,
      updatedAt: new Date(),
    })
  );

  // Mark pendingDelete and Delete
  await assertSucceeds(
    db.collection("videos").doc("vid_owner_1").set({
      ...basePayload,
      title: "Updated Director Cut",
      published: false,
      pendingDelete: true,
      updatedAt: new Date(),
    })
  );
  await assertSucceeds(db.collection("videos").doc("vid_owner_1").delete());
});

test("5. Shadow Update & Value Poisoning: invalid category or extra ghost fields rejected", async () => {
  const db = testEnv.unauthenticatedContext().firestore();
  const sessionId = "owner_sess_5";
  await assertSucceeds(
    db.collection("admin_sessions").doc(sessionId).set({
      sessionId,
      passkeyHash: VALID_PW_HASH,
      createdAt: new Date(),
    })
  );

  // Invalid category rejected
  await assertFails(
    db
      .collection("videos")
      .doc("bad_cat_vid")
      .set(validVideoPayload(sessionId, { category: "Invalid Category" }))
  );

  // Extra ghost field rejected
  await assertFails(
    db
      .collection("videos")
      .doc("ghost_vid")
      .set(validVideoPayload(sessionId, { extraAdminFlag: true }))
  );
});

test("6. Public visitor can save, list, update notes, and delete saved prompts without login", async () => {
  const db = testEnv.unauthenticatedContext().firestore();
  const visitorId = "visitor_device_abc123";
  const now = new Date(Date.now() - 2000);

  const savedPayload = {
    visitorId,
    videoId: "vishnu_showcase_1",
    videoTitle: "Neo-Tokyo Rain",
    prompt: "Cinematic cyberpunk rain 8k",
    category: "Cinematic AI Videos",
    thumbnailUrl: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
    aiModel: "Veo 2 Cinema",
    notes: "Initial note",
    createdAt: now,
    updatedAt: now,
  };

  await assertSucceeds(
    db
      .collection("visitors")
      .doc(visitorId)
      .collection("saved_prompts")
      .doc("saved_1")
      .set(savedPayload)
  );

  await assertSucceeds(
    db
      .collection("visitors")
      .doc(visitorId)
      .collection("saved_prompts")
      .where("visitorId", "==", visitorId)
      .get()
  );

  await assertSucceeds(
    db
      .collection("visitors")
      .doc(visitorId)
      .collection("saved_prompts")
      .doc("saved_1")
      .set({
        ...savedPayload,
        notes: "Updated note for Sora",
        updatedAt: new Date(),
      })
  );

  await assertSucceeds(
    db
      .collection("visitors")
      .doc(visitorId)
      .collection("saved_prompts")
      .doc("saved_1")
      .delete()
  );
});
