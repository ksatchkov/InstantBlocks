package com.slymask3.instantblocks.gui.instant;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.slymask3.instantblocks.InstantBlocks;
import com.slymask3.instantblocks.block.BlockInstant;
import com.slymask3.instantblocks.gui.GuiInstant;
import com.slymask3.instantblocks.network.PacketHandler;
import com.slymask3.instantblocks.network.PacketTree;
import com.slymask3.instantblocks.reference.Strings;
import com.slymask3.instantblocks.tileentity.TileEntityTree;
import com.slymask3.instantblocks.util.BuildHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GuiTree extends GuiInstant {
	protected final TileEntityTree tileEntity;

	private TreeList treeList;
    private int selected = -1;
    private int selectedTree;
    private int[] trees = {0, 1, 2, 3, 4, 5, 6};

	private Checkbox fullLog, fullLeaves, air;

	public GuiTree(Player player, Level world, int x, int y, int z) {
		super(player, world, x, y, z, "Instant Statue");
		this.tileEntity = (TileEntityTree)world.getBlockEntity(new BlockPos(x,y,z));
	}

	@Override
	public void init() {
		super.init();

		this.fullLog = new Checkbox(this.width / 2 + 4, 50, 150, 20, new TextComponent("Hollow Logs"), true);
		this.fullLeaves = new Checkbox(this.width / 2 + 4, 72, 150, 20, new TextComponent("Hollow Leaves"), true);
		this.air = new Checkbox(this.width / 2 + 4, 94, 150, 20, new TextComponent("Air Blocks Inside"), true);

		this.treeList = new TreeList(this.minecraft);
		this.addWidget(this.treeList);

		InstantBlocks.LOGGER.info("treeList.children(): " + treeList.children());

		this.done.active = false;

		this.addRenderableWidget(this.fullLog);
		this.addRenderableWidget(this.fullLeaves);
		this.addRenderableWidget(this.air);
		this.addRenderableWidget(this.treeList);
	}

	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, "Options:", this.width / 2 + 4, 40, 10526880);
		this.font.draw(poseStack, "Tree Type:", this.width / 2 - 2 - 150, 40, 10526880);
	}

	public void sendInfo() {
		PacketHandler.sendToServer(new PacketTree(this.x, this.y, this.z, selectedTree, !fullLog.selected(), !fullLeaves.selected(), air.selected()));

		BlockInstant block = (BlockInstant)BuildHelper.getBlock(world,x,y,z);
		block.setCreateMessage(Strings.CREATE_TREE.replace("%tree%",treeToString(selectedTree)));
		block.afterBuild(world,x,y,z,player);
	}
	
	public String treeToString(int tree) {
    	switch(tree) {
			case 0: return "Huge Oak Tree";
			case 1: return "Huge Spruce Tree";
			case 2: return "Huge Birch Tree";
			case 3: return "Huge Jungle Tree";
			case 4: return "Huge Acacia Tree";
			case 5: return "Huge Dark Oak Tree";
			case 6: return "Huge Glass Tree";
			default: return "Error";
    	}
    }

    public void setSelected(int index) {
		InstantBlocks.LOGGER.info("setSelected: " + index);
        this.selected=index;
        if((index>=0 && index<=6)) {
            this.selectedTree=trees[selected];
            this.done.active = true;
        } else {
            this.selectedTree=-1;
            this.done.active = false;
        }
    }

	public int getSelected() {
		return this.selected;
	}

	@OnlyIn(Dist.CLIENT)
	class TreeList extends ObjectSelectionList<TreeList.Entry> {
		public TreeList(Minecraft minecraft) {
			super(minecraft, 144, 120, 50, 170, 18);
			this.setLeftPos(GuiTree.this.width / 2 - 4 - 150);
			this.setRenderHeader(false, 0);
			this.setRenderTopAndBottom(false);
			this.setRenderSelection(true);
			//this.setRenderBackground(false);
			for(int tree : GuiTree.this.trees) {
				GuiTree.TreeList.Entry entry = new GuiTree.TreeList.Entry(tree);
				this.addEntry(entry);
			}
			if (this.getSelected() != null) {
				this.centerScrollOn(this.getSelected());
			}
		}

		protected int getScrollbarPosition() {
			return this.x0 + this.width;
		}

		protected void renderList(PoseStack p_93452_, int p_93453_, int p_93454_, int p_93455_, int p_93456_, float p_93457_) {
			super.renderList(p_93452_, p_93453_, p_93454_, p_93455_, p_93456_, p_93457_);

			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = tesselator.getBuilder();

			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			//RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
			tesselator.end();

			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.vertex((double)this.x0, (double)(this.y0 - 10), 0.0D).color(255, 255, 255, 0).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)(this.y0 - 10), 0.0D).color(255, 255, 255, 0).endVertex();
			bufferbuilder.vertex((double)this.x1, (double)(this.y0), 0.0D).color(255, 255, 255, 0).endVertex();
			bufferbuilder.vertex((double)this.x0, (double)(this.y0), 0.0D).color(255, 255, 255, 0).endVertex();
			tesselator.end();
		}

		protected boolean isFocused() {
			return GuiTree.this.getFocused() == this;
		}

		@OnlyIn(Dist.CLIENT)
		public class Entry extends ObjectSelectionList.Entry<GuiTree.TreeList.Entry> {
			final int index;

			public Entry(int index) {
				this.index = index;
			}

			public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
				if(top > GuiTree.TreeList.this.y0 - 5 && top + entryHeight < GuiTree.TreeList.this.y1 + 5) {
					String string = GuiTree.this.treeToString(index);
					GuiTree.this.font.drawShadow(poseStack, string, (float)(left + entryWidth / 2 - GuiTree.this.font.width(string) / 2), top, GuiTree.this.getSelected() == this.index ? 0x00AA00 : 16777215, true);
				}
			}

			public boolean mouseClicked(double p_96122_, double p_96123_, int p_96124_) {
				InstantBlocks.LOGGER.info("mouseClicked: " + p_96124_);
				if (p_96124_ == 0) {
					GuiTree.this.setSelected(this.index);
					return true;
				} else {
					return false;
				}
			}

			public Component getNarration() {
				return new TranslatableComponent("narrator.select", GuiTree.this.treeToString(index));
			}
		}
	}
}