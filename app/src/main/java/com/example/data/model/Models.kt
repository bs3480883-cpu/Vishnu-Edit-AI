package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class VideoCategory(
    val displayName: String,
    val subtitle: String,
    val badgeLabel: String
) {
    CINEMATIC(
        displayName = "Cinematic AI Videos",
        subtitle = "Anamorphic 8K film sequences, dramatic lighting & sci-fi worlds",
        badgeLabel = "CINEMATIC"
    ),
    ANIMAL(
        displayName = "Animal AI Videos",
        subtitle = "Hyper-detailed wildlife, mythical creatures & nature motion",
        badgeLabel = "ANIMAL AI"
    ),
    REALISTIC(
        displayName = "Realistic AI Videos",
        subtitle = "Photorealistic human portraits, architecture & urban life",
        badgeLabel = "REALISTIC"
    ),
    ANIMATION_3D(
        displayName = "3D Animation",
        subtitle = "Pixar-grade stylized characters, CGI worlds & octane renders",
        badgeLabel = "3D ANIMATION"
    ),
    VFX(
        displayName = "VFX Videos",
        subtitle = "Particle simulations, fluid dynamics, portals & destruction",
        badgeLabel = "VFX STUDIO"
    ),
    SHORT_FILMS(
        displayName = "AI Short Films",
        subtitle = "Multi-shot narrative storytelling, voiceover & emotional arcs",
        badgeLabel = "SHORT FILM"
    );

    companion object {
        val ALL_NAMES: List<String> = entries.map { it.displayName }

        fun fromDisplayName(name: String): VideoCategory {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
                ?: CINEMATIC
        }
    }
}

@IgnoreExtraProperties
data class VideoShowcase(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val prompt: String = "",
    val category: String = VideoCategory.CINEMATIC.displayName,
    val published: Boolean = true,
    val featured: Boolean = false,
    val popularPrompt: Boolean = false,
    val durationSeconds: Long = 15L,
    val aspectRatio: String = "16:9",
    val aiModel: String = "Veo 2 Cinema",
    val authorId: String = StudioMediaCatalog.STUDIO_AUTHOR_ID,
    val adminSessionId: String = "",
    val pendingDelete: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    @get:Exclude
    val formattedDuration: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return "%02d:%02d".format(mins, secs)
        }
}

@IgnoreExtraProperties
data class SavedPrompt(
    @DocumentId val id: String = "",
    val visitorId: String = "",
    val videoId: String = "",
    val videoTitle: String = "",
    val prompt: String = "",
    val category: String = VideoCategory.CINEMATIC.displayName,
    val thumbnailUrl: String = "",
    val aiModel: String = "Veo 2 Cinema",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class StudioMediaPreset(
    val label: String,
    val category: VideoCategory,
    val videoUrl: String,
    val thumbnailUrl: String,
    val defaultTitle: String,
    val defaultDescription: String,
    val defaultPrompt: String,
    val durationSeconds: Long,
    val aspectRatio: String,
    val aiModel: String,
    val featured: Boolean,
    val popularPrompt: Boolean
)

object StudioMediaCatalog {
    const val OWNER_EMAIL = "bs3480883@gmail.com"
    const val STUDIO_AUTHOR_ID = "vishnu_owner_studio"
    const val DEFAULT_ADMIN_PASSWORD = "Vishnu@2026"

    val presets: List<StudioMediaPreset> = listOf(
        StudioMediaPreset(
            label = "Cyberpunk Neon Rain Metropolis (16:9)",
            category = VideoCategory.CINEMATIC,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Neo-Tokyo Rain & Holographic Horizon",
            defaultDescription = "Anamorphic 2.39:1 widescreen AI cinema piece exploring rain-slicked cyberpunk streets lit by volumetric electric blue and violet neon signage.",
            defaultPrompt = "Ultra-cinematic wide tracking shot through a futuristic cyberpunk metropolis at night during heavy rain, glowing electric blue (#287BFF) and neon purple (#9146FF) holographic billboards reflecting on wet asphalt, anamorphic lens flare, volumetric fog, shot on ARRI Alexa 65, 8k photorealistic, 24fps, smooth dolly motion.",
            durationSeconds = 15L,
            aspectRatio = "16:9",
            aiModel = "Veo 2 Cinema",
            featured = true,
            popularPrompt = true
        ),
        StudioMediaPreset(
            label = "Bioluminescent Snow Leopard (16:9)",
            category = VideoCategory.ANIMAL,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1456926631375-92c8ce872def?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Bioluminescent Snow Leopard in Nocturnal Glacier",
            defaultDescription = "Hyper-detailed AI wildlife cinematography capturing a snow leopard with subtle bioluminescent fur markings stalking across an obsidian ice ridge.",
            defaultPrompt = "National Geographic style macro tracking shot of a majestic snow leopard walking silently across a dark obsidian glacier at twilight, subtle electric blue bioluminescent frost particles floating in the air, razor-sharp fur micro-details, golden-blue rim lighting, 85mm f/1.4 prime lens, slow motion 60fps.",
            durationSeconds = 15L,
            aspectRatio = "16:9",
            aiModel = "Sora Studio Pro",
            featured = true,
            popularPrompt = true
        ),
        StudioMediaPreset(
            label = "Hyper-Real Rainy Tokyo Portrait & Streetcar (16:9)",
            category = VideoCategory.REALISTIC,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Midnight Shinjuku Express — Photorealistic Study",
            defaultDescription = "True-to-life optical physics, natural skin subsurface scattering, and rain droplets on vintage glass captured via next-gen realistic AI synthesis.",
            defaultPrompt = "Photorealistic close-up cinematic shot through a rain-streaked train window at night, warm tungsten interior lighting contrasting with cool cobalt blue city lights outside, realistic depth of field, natural film grain, Kodak Vision3 500T color grading, 4k realistic motion.",
            durationSeconds = 60L,
            aspectRatio = "16:9",
            aiModel = "Runway Gen-3 Alpha",
            featured = false,
            popularPrompt = true
        ),
        StudioMediaPreset(
            label = "Stylized 3D Mecha Bot Workshop (16:9)",
            category = VideoCategory.ANIMATION_3D,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoy.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Aether-9: Miniature Clockwork Explorer",
            defaultDescription = "Feature-animation quality 3D render showcasing physically based materials, subsurface glow, and expressive character animation.",
            defaultPrompt = "Pixar and Unreal Engine 5 style 3D animated scene of a tiny brushed-titanium robot with glowing sapphire optical sensors assembling a floating holographic star map inside a cozy futuristic workshop, soft global illumination, Octane Render, ray-traced reflections, 4k 3D animation.",
            durationSeconds = 15L,
            aspectRatio = "16:9",
            aiModel = "Kling 1.6 Pro 3D",
            featured = true,
            popularPrompt = false
        ),
        StudioMediaPreset(
            label = "Quantum Singularity Portal VFX (16:9)",
            category = VideoCategory.VFX,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Quantum Horizon: Particle & Fluid Singularity",
            defaultDescription = "High-energy Houdini-style AI visual effects simulation combining plasma filaments, gravitational lensing, and metallic shockwaves.",
            defaultPrompt = "Epic VFX breakdown shot of a massive gravitational singularity portal opening above a dark desert salt flat, swirling electric blue and ultraviolet plasma arcs, zero-gravity levitating basalt rocks, realistic smoke and ember fluid simulation, IMAX 70mm scale, high dynamic range.",
            durationSeconds = 15L,
            aspectRatio = "16:9",
            aiModel = "Veo 2 VFX Engine",
            featured = false,
            popularPrompt = true
        ),
        StudioMediaPreset(
            label = "Chronos Odyssey — AI Short Film (16:9)",
            category = VideoCategory.SHORT_FILMS,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=85",
            defaultTitle = "Chronos Odyssey: The Last Starlight",
            defaultDescription = "Award-style AI short film combining coherent multi-angle character continuity, atmospheric worldbuilding, and orchestral pacing.",
            defaultPrompt = "Cinematic sci-fi short film opening sequence: a lone astronaut in a sleek matte-black exosuit standing on the rim of a glowing crystalline crater orbiting a binary purple star, slow dramatic camera crane up revealing an ancient monolithic starship emerging from orbital clouds, Denis Villeneuve inspired cinematography, 8k.",
            durationSeconds = 52L,
            aspectRatio = "2.39:1",
            aiModel = "Veo 2 Cinema",
            featured = true,
            popularPrompt = true
        )
    )
}
