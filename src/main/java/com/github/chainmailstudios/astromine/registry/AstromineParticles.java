package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.client.particle.RocketFlameParticle;
import com.github.chainmailstudios.astromine.mixin.CrackParticleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class AstromineParticles {
	public static final DefaultParticleType SPACE_SLIME = register("space_slime", false);
	public static final DefaultParticleType ROCKET_FLAME = register("rocket_flame", true);

	/**
	 * Registers a new {@link DefaultParticleType} instance under the given name.
	 *
	 * @param name       Name of {@link DefaultParticleType} to register
	 * @param alwaysShow Whether or not the particle should always appear visible
	 * @return Registered {@link DefaultParticleType}
	 */
	public static DefaultParticleType register(String name, boolean alwaysShow) {
		return Registry.register(Registry.PARTICLE_TYPE, AstromineCommon.identifier(name), FabricParticleTypes.simple(alwaysShow));
	}

	@Environment(EnvType.CLIENT)
	public static void initialize() {
		ParticleFactoryRegistry.getInstance().register(SPACE_SLIME, (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> CrackParticleAccessor.createCrackParticle(world, x, y, z, new ItemStack(AstromineItems.SPACE_SLIME_BALL)));
		ParticleFactoryRegistry.getInstance().register(ROCKET_FLAME, provider -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
			RocketFlameParticle particle = new RocketFlameParticle(world, x, y, z, velocityX, velocityY, velocityZ);
			particle.setSprite(provider);
			return particle;
		});
	}
}
