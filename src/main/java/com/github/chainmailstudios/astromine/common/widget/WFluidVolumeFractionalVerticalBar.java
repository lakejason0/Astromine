package com.github.chainmailstudios.astromine.common.widget;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.client.BaseRenderer;
import com.github.chainmailstudios.astromine.client.render.SpriteRenderer;
import com.github.chainmailstudios.astromine.common.utilities.FluidUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import spinnery.client.utility.ScissorArea;

import java.util.function.Supplier;

public class WFluidVolumeFractionalVerticalBar extends WFractionalVerticalBar {
	private Supplier<FluidVolume> volume;

	private final Identifier FLUID_BACKGROUND = AstromineCommon.identifier("textures/widget/fluid_volume_fractional_vertical_bar_background.png");

	public WFluidVolumeFractionalVerticalBar() {
		super();

		setUnit(new TranslatableText("text.astromine.fluid"));

		setBackgroundTexture(FLUID_BACKGROUND);
	}

	@Override
	public Identifier getBackgroundTexture() {
		return FLUID_BACKGROUND;
	}

	public FluidVolume getFluidVolume() {
		return volume.get();
	}

	public <W extends WFractionalVerticalBar> W setFluidVolume(Supplier<FluidVolume> volume) {
		setProgressFraction(volume.get()::getFraction);
		setLimitFraction(volume.get()::getSize);

		this.volume = volume;
		return (W) this;
	}

	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
		if (isHidden()) {
			return;
		}

		float x = getX();
		float y = getY();
		float z = getZ();

		float sX = getWidth();
		float sY = getHeight();

		float sBGY = (((sY / getLimitFraction().get().floatValue()) * getProgressFraction().get().floatValue()));

		BaseRenderer.drawTexturedQuad(matrices, provider, getX(), getY(), z, getWidth(), getHeight(), getBackgroundTexture());

		if (getFluidVolume().getFluid() != Fluids.EMPTY) {
			SpriteRenderer.beginPass()
					.setup(provider, RenderLayer.getSolid())
					.sprite(FluidUtilities.texture(getFluidVolume().getFluid())[0])
					.color(FluidUtilities.color(MinecraftClient.getInstance().player, getFluidVolume().getFluid()))
					.light(0x00f000f0)
					.overlay(OverlayTexture.DEFAULT_UV)
					.alpha(0xff)
					.normal(matrices.peek().getNormal(), 0, 0, 0)
					.position(matrices.peek().getModel(), x + 1, y + 1, x + sX - 1, y + (sBGY), z)
					.next();
		}

		if (isFocused()) {
			long progressNumerator = getProgressFraction().get().getNumerator();
			long progressDenominator = getProgressFraction().get().getDenominator();

			long limitNumerator = getLimitFraction().get().getNumerator();
			long limitDenominator = getLimitFraction().get().getDenominator();

			getTooltipText().setText(new TranslatableText("text.astromine.tooltip.fractional_bar", progressNumerator, progressDenominator, limitNumerator, limitDenominator, getUnit().getString()));

			getTooltip().draw(matrices, provider);

			RenderSystem.translatef(0, 0, 250);

			getTooltipText().draw(matrices, provider);

			RenderSystem.translatef(0, 0, -250);
		}
	}
}
