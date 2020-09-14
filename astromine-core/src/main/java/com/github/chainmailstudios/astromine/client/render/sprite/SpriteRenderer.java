/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.client.render.sprite;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;

@Environment(EnvType.CLIENT)
public class SpriteRenderer {
	public static RenderPass beginPass() {
		return new RenderPass();
	}

	public static class RenderPass {
		float x1 = 0;
		float x2 = 1;
		float y1 = 0;
		float y2 = 0;
		float z1 = 0;
		float uStart = 0F;
		float uEnd = 1F;
		float vStart = 0F;
		float vEnd = 1F;
		int u = 0;
		int v = 1;
		int r = 0xff;
		int g = 0xff;
		int b = 0xff;
		int a = 0xff;
		int l = 0;
		float nX = 0;
		float nY = 0;
		float nZ = 0;
		TextureAtlasSprite sprite;
		IVertexBuilder consumer;
		IRenderTypeBuffer consumers;
		MatrixStack matrices;
		Matrix4f model;
		Matrix3f normal;
		RenderType layer;

		private RenderPass() {}

		public RenderPass setup(IRenderTypeBuffer consumers, RenderType layer) {
			this.consumers = consumers;
			this.setup(consumers.getBuffer(layer), layer);

			return this;
		}

		public RenderPass setup(IVertexBuilder consumer, RenderType layer) {
			this.consumer = consumer;
			this.matrices = new MatrixStack();
			this.layer = layer;

			return this;
		}

		public RenderPass setup(IRenderTypeBuffer consumers, MatrixStack matrices, RenderType layer) {
			this.consumers = consumers;
			this.consumer = consumers.getBuffer(layer);
			this.matrices = matrices;
			this.layer = layer;

			return this;
		}

		public RenderPass position(Matrix4f model, float x1, float y1, float x2, float y2, float z1) {
			this.position(x1, y1, x2, y2, z1);
			this.model = model;

			return this;
		}

		public RenderPass position(float x1, float y1, float x2, float y2, float z1) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.z1 = z1;

			return this;
		}

		public RenderPass sprite(TextureAtlasSprite sprite) {
			this.uStart = sprite.getU0();
			this.uEnd = sprite.getU1();
			this.vStart = sprite.getV0();
			this.vEnd = sprite.getV1();

			this.sprite = sprite;

			return this;
		}

		public RenderPass sprite(float uStart, float uEnd, float vStart, float vEnd) {
			this.uStart = uStart;
			this.uEnd = uEnd;
			this.vStart = vStart;
			this.vEnd = vEnd;

			return this;
		}

		public RenderPass overlay(int uv) {
			return this.overlay(uv & '\uffff', uv >> 16 & '\uffff');
		}

		public RenderPass overlay(int u, int v) {
			this.u = u;
			this.v = v;

			return this;
		}

		public RenderPass color(int color) {
			this.r = ((color >> 16) & 0xFF);
			this.g = ((color >> 8) & 0xFF);
			this.b = (color & 0xFF);

			return this;
		}

		public RenderPass color(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;

			return this;
		}

		public RenderPass alpha(int a) {
			this.a = a;

			return this;
		}

		public RenderPass light(int l) {
			this.l = l;

			return this;
		}

		public RenderPass normal(Matrix3f normal, float nX, float nY, float nZ) {
			this.normal(nX, nY, nZ);
			this.normal = normal;

			return this;
		}

		public RenderPass normal(float nX, float nY, float nZ) {
			this.nX = nX;
			this.nY = nY;
			this.nZ = nZ;

			return this;
		}

		public void next() {
			if (this.sprite == null) {
				throw new RuntimeException("Invalid Sprite!");
			}

			next(sprite.getName());
		}

		public void next(ResourceLocation texture) {
			if (this.consumer == null) {
				throw new RuntimeException("Invalid VertexConsumer!");
			}
			if (this.matrices == null) {
				throw new RuntimeException("Invalid MatrixStack!");
			}
			if (this.sprite == null) {
				throw new RuntimeException("Invalid Sprite!");
			}

			if (this.model == null) {
				this.model = this.matrices.last().pose();
			}
			if (this.normal == null) {
				this.normal = this.matrices.last().normal();
			}

			float sX = sprite.getWidth();
			float sY = sprite.getHeight();

			Minecraft.getInstance().getTextureManager().bind(texture);

			for (float y = y1; y < y2; y += Math.min(y2 - y, sY)) {
				for (float x = x1; x < x2; x += Math.min(x2 - x, sX)) {
					float nSX = Math.min(x2 - x, sX);
					float nSY = Math.min(y2 - y, sY);

					boolean isOverX = nSX < sX;
					boolean isOverY = nSY < sY;

					float dX = 0;
					float dY = 0;

					if (isOverX) {
						dX = (uEnd - uStart) * (1 - (nSX / sX));
					}

					if (isOverY) {
						dY = (uEnd - uStart) * (1 - (nSY / sY));
					}

					this.consumer = consumers.getBuffer(RenderType.solid());

					this.consumer.vertex(this.model, x, y + nSY, z1).color(this.r, this.g, this.b, this.a).uv(this.uStart, this.vEnd - dY).overlayCoords(this.u, this.v).uv2(this.l).normal(this.normal, this.nX, this.nY, this.nZ).endVertex();
					this.consumer.vertex(this.model, x + nSX, y + nSY, z1).color(this.r, this.g, this.b, this.a).uv(this.uEnd - dX, this.vEnd - dY).overlayCoords(this.u, this.v).uv2(this.l).normal(this.normal, this.nX, this.nY, this.nZ).endVertex();
					this.consumer.vertex(this.model, x + nSX, y, z1).color(this.r, this.g, this.b, this.a).uv(this.uEnd - dX, this.vStart).overlayCoords(this.u, this.v).uv2(this.l).normal(this.normal, this.nX, this.nY, this.nZ).endVertex();
					this.consumer.vertex(this.model, x, y, z1).color(this.r, this.g, this.b, this.a).uv(this.uStart, this.vStart).overlayCoords(this.u, this.v).uv2(this.l).normal(this.normal, this.nX, this.nY, this.nZ).endVertex();
				}
			}

		}
	}
}
