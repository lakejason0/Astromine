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

package com.github.chainmailstudios.astromine.common.entity.projectile;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.registry.AstromineEntityTypes;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BulletEntity extends PersistentProjectileEntity {
	public Identifier texture = AstromineCommon.identifier("textures/entity/projectiles/bullet.png");

	public BulletEntity(World world) {
		super(AstromineEntityTypes.BULLET_ENTITY_TYPE, world);
	}

	public BulletEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public BulletEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
		super(type, x, y, z, world);
	}

	public BulletEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
		super(type, owner, world);
	}

	@Override
	protected void checkBlockCollision() {
		Box box = this.getBoundingBox();
		BlockPos positionA = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
		BlockPos positionB = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (this.world.isRegionLoaded(positionA, positionB)) {
			for (int i = positionA.getX(); i <= positionB.getX(); ++i) {
				for (int j = positionA.getY(); j <= positionB.getY(); ++j) {
					for (int k = positionA.getZ(); k <= positionB.getZ(); ++k) {
						mutable.set(i, j, k);
						BlockState blockState = this.world.getBlockState(mutable);

						try {
							blockState.onEntityCollision(this.world, mutable, this);

							if (blockState.getBlock() instanceof AbstractGlassBlock) {
								this.world.breakBlock(mutable, true);
							}
						} catch (Throwable oops) {
							CrashReport crashReport = CrashReport.create(oops, "Colliding entity with block");
							CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
							CrashReportSection.addBlockInfo(crashReportSection, mutable, blockState);
							throw new CrashException(crashReport);
						}
					}
				}
			}
		}
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);
		this.remove();
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.remove();
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		this.remove();
	}

	@Override
	protected void onBlockCollision(BlockState state) {
		Block block = state.getBlock();

		if (block instanceof AbstractGlassBlock) {
			this.world.setBlockState(this.getBlockPos(), Blocks.AIR.getDefaultState());
		}
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

	public Identifier getTexture() {
		return this.texture;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
