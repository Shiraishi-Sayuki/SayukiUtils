/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sayuki.utils.lan.LanServer;
import com.sayuki.utils.lan.LanServerValues;
import com.sayuki.utils.lan.LanSettings;
import com.sayuki.utils.lan.LanState;
import com.sayuki.utils.lan.TunnelType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.HttpUtil;

@Mixin(ShareToLanScreen.class)
public abstract class ShareToLanScreenMixin extends Screen implements ShareToLanScreenAccessor {
    @Unique
    private boolean initialized = false;
    @Unique
    private LanState lanState;
    @Unique
    private Button perWorldLoadButton;
    @Unique
    private Button perWorldClearButton;
    @Unique
    private Button globalLoadButton;
    @Unique
    private Button globalClearButton;
    @Unique
    private CycleButton<net.minecraft.world.level.GameType> gameModeButton;
    @Unique
    private CycleButton<Boolean> onlineModeButton;
    @Unique
    private CycleButton<Boolean> pvpEnabledButton;
    @Unique
    private CycleButton<TunnelType> tunnelButton;
    @Unique
    private EditBox maxPlayersField;
    @Unique
    private EditBox motdField;
    @Unique
    private Button startSaveButton;
    @Unique
    private MultiLineTextWidget explanationText;
    @Unique
    private boolean onlineMode;
    @Unique
    private boolean pvpEnabled;
    @Unique
    private TunnelType tunnel;
    @Unique
    private int rawPort;
    @Unique
    private boolean portValid = true;
    @Unique
    private int maxPlayers;
    @Unique
    private boolean maxPlayersValid = true;
    @Unique
    private String rawMotd;

    @Unique
    private static final Component PER_WORLD_TEXT = Component.translatable("sayukiutils.lan.perWorld");
    @Unique
    private static final Component GLOBAL_TEXT = Component.translatable("sayukiutils.lan.global");
    @Unique
    private static final Component SYSTEM_TEXT = Component.translatable("sayukiutils.lan.system");
    @Unique
    private static final Component LOAD_TEXT = Component.translatable("sayukiutils.lan.load");
    @Unique
    private static final Component CLEAR_TEXT = Component.translatable("sayukiutils.lan.clear");
    @Unique
    private static final Component SAVE_TEXT = Component.translatable("sayukiutils.lan.save");
    @Unique
    private static final Component START_TEXT = Component.translatable("sayukiutils.lan.start");
    @Unique
    private static final Component STOP_TEXT = Component.translatable("sayukiutils.lan.stop");
    @Unique
    private static final Component ONLINE_MODE_TEXT = Component.translatable("sayukiutils.lan.onlineMode");
    @Unique
    private static final Component PVP_ENABLED_TEXT = Component.translatable("sayukiutils.lan.pvpEnabled");
    @Unique
    private static final Component TUNNEL_TEXT = Component.translatable("sayukiutils.lan.tunnel");
    @Unique
    private static final Component MAX_PLAYERS_TEXT = Component.translatable("sayukiutils.lan.maxPlayers");
    @Unique
    private static final Component INVALID_MAX_PLAYERS_TEXT = Component.translatable("sayukiutils.lan.maxPlayers.invalid");
    @Unique
    private static final Component MOTD_TEXT = Component.translatable("sayukiutils.lan.motd");
    @Unique
    private static final Component MOTD_DESCRIPTION_TEXT = Component.translatable("sayukiutils.lan.motd.description");
    @Unique
    private static final Component CLEAR_PERWORLD_QUESTION_TEXT = Component.translatable("sayukiutils.lan.clear.perWorld.question");
    @Unique
    private static final Component CLEAR_GLOBAL_QUESTION_TEXT = Component.translatable("sayukiutils.lan.clear.global.question");
    @Unique
    private static final Component EXPLANATION_TEXT = Component.translatable("sayukiutils.lan.explanation");
    @Unique
    private static final Component LOAD_SYSTEM_TEXT = Component.translatable("sayukiutils.lan.load.system");
    @Unique
    private static final Component PORT_TEXT = Component.translatable("sayukiutils.lan.port");

    protected ShareToLanScreenMixin(Component title) {
        super(title);
    }

    @Unique
    // 開始または保存可能か確認
    private boolean canStartOrSave() {
        return this.portValid && this.maxPlayersValid;
    }

    @Unique
    // LAN設定値を読み込み
    private void loadLanSettingsValues(@Nullable LanSettings settings) {
        if (settings == null) return;
        this.setGameMode(settings.gameType);
        this.onlineMode = settings.onlineMode;
        this.pvpEnabled = settings.pvpEnabled;
        this.tunnel = settings.tunnel;
        this.rawPort = settings.port;
        this.setPort(settings.port != -1 ? settings.port : HttpUtil.getAvailablePort());
        this.maxPlayers = settings.maxPlayers;
        this.rawMotd = settings.motd;
    }

    @Unique
    // LAN設定を読み込み
    private void loadLanSettings(@Nullable LanSettings settings) {
        this.loadLanSettingsValues(settings);
        this.gameModeButton.setValue(this.getGameMode());
        this.onlineModeButton.setValue(this.onlineMode);
        this.pvpEnabledButton.setValue(this.pvpEnabled);
        this.tunnelButton.setValue(this.tunnel);
        if (this.getPortEdit() != null) this.getPortEdit().setValue(this.rawPort != -1 ? Integer.toString(this.getPort()) : "");
        this.maxPlayersField.setValue(Integer.toString(this.maxPlayers));
        this.motdField.setValue(this.rawMotd);
    }

    @Unique
    // LAN設定を保存
    private LanSettings saveLanSettings() {
        return new LanSettings(this.getGameMode(), this.onlineMode, this.pvpEnabled, this.tunnel,
                this.getPortEdit() != null && this.getPortEdit().getValue().isBlank() ? -1 : this.getPort(),
                this.maxPlayers, this.rawMotd);
    }

    @Unique
    // 設定ボタンを更新
    private void updateSettingsButtons() {
        boolean savedPerWorld = this.lanState.getLanSettings() != null;
        this.perWorldLoadButton.active = savedPerWorld;
        this.perWorldClearButton.active = savedPerWorld;
        boolean savedGlobal = this.lanState.getGlobalSettings() != null;
        this.globalLoadButton.active = savedGlobal;
        this.globalClearButton.active = savedGlobal;
    }

    @Inject(method = "init", at = @At("HEAD"))
    // init前処理
    private void preInit(CallbackInfo ci) {
        var server = Minecraft.getInstance().getSingleplayerServer();

        if (!this.initialized) {
            this.lanState = server.overworld().getDataStorage().computeIfAbsent(
                    LanState.factory(), LanState.DATA_NAME);

            if (server.isPublished()) {
                LanServerValues serverValues = (LanServerValues) server;
                this.setGameMode(server.getDefaultGameType());
                this.onlineMode = server.usesAuthentication();
                this.pvpEnabled = server.isPvpAllowed();
                this.tunnel = serverValues.getTunnelType();
                this.setPort(server.getPort());
                this.rawPort = this.getPort();
                this.maxPlayers = server.getMaxPlayers();
                this.rawMotd = serverValues.getRawMotd();
            } else if (this.lanState.getLanSettings() != null) {
                this.loadLanSettingsValues(this.lanState.getLanSettings());
            } else {
                this.loadLanSettingsValues(LanSettings.systemDefaults(server));
            }

            this.initialized = true;
        }

        int cx = this.width / 2;

        int pwLabelWidth = this.font.width(PER_WORLD_TEXT);
        int pwBtnX = cx - 155 + pwLabelWidth + 8;
        int btnW = 56;
        int btnGap = 2;

        this.addRenderableWidget(Button.builder(SAVE_TEXT, button -> {
            this.lanState.setLanSettings(this.saveLanSettings());
            this.updateSettingsButtons();
        }).bounds(pwBtnX, 8, btnW, 20).build());

        this.perWorldLoadButton = this.addRenderableWidget(Button.builder(LOAD_TEXT,
                button -> this.loadLanSettings(this.lanState.getLanSettings()))
                .bounds(pwBtnX + btnW + btnGap, 8, btnW, 20).build());

        this.perWorldClearButton = this.addRenderableWidget(Button.builder(CLEAR_TEXT,
                button -> this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        this.lanState.setLanSettings(null);
                        this.updateSettingsButtons();
                    }
                    this.minecraft.setScreen(this);
                }, CLEAR_PERWORLD_QUESTION_TEXT, CommonComponents.EMPTY, CLEAR_TEXT, CommonComponents.GUI_CANCEL)))
                .bounds(pwBtnX + 2 * (btnW + btnGap), 8, btnW, 20).build());

        int glLabelWidth = this.font.width(GLOBAL_TEXT);
        int glBtnX = cx - 155 + glLabelWidth + 8;

        this.addRenderableWidget(Button.builder(SAVE_TEXT, button -> {
            this.lanState.setGlobalSettings(this.saveLanSettings());
            this.updateSettingsButtons();
        }).bounds(glBtnX, 30, btnW, 20).build());

        this.globalLoadButton = this.addRenderableWidget(Button.builder(LOAD_TEXT,
                button -> this.loadLanSettings(this.lanState.getGlobalSettings()))
                .bounds(glBtnX + btnW + btnGap, 30, btnW, 20).build());

        this.globalClearButton = this.addRenderableWidget(Button.builder(CLEAR_TEXT,
                button -> this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        this.lanState.setGlobalSettings(null);
                        this.updateSettingsButtons();
                    }
                    this.minecraft.setScreen(this);
                }, CLEAR_GLOBAL_QUESTION_TEXT, CommonComponents.EMPTY, CLEAR_TEXT, CommonComponents.GUI_CANCEL)))
                .bounds(glBtnX + 2 * (btnW + btnGap), 30, btnW, 20).build());

        int sysLabelWidth = this.font.width(SYSTEM_TEXT);
        int sysBtnX = cx - 155 + sysLabelWidth + 8;

        this.addRenderableWidget(Button.builder(LOAD_SYSTEM_TEXT,
                button -> this.loadLanSettings(LanSettings.systemDefaults(server)))
                .bounds(sysBtnX, 52, cx + 155 - sysBtnX, 20).build());

        this.updateSettingsButtons();
    }

    @Inject(method = "init", at = @At("TAIL"))
    // init後処理
    private void postInit(CallbackInfo ci) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        boolean alreadyPublished = server.isPublished();
        int cx = this.width / 2;

        this.gameModeButton = (CycleButton<net.minecraft.world.level.GameType>) this.children().get(7);
        this.gameModeButton.setWidth(310);
        this.gameModeButton.setY(90);

        var allowCommandsButton = this.children().get(8);
        this.removeWidget(allowCommandsButton);

        this.onlineModeButton = this.addRenderableWidget(
                CycleButton.onOffBuilder(this.onlineMode).create(cx - 155, 114, 150, 20,
                        ONLINE_MODE_TEXT, (button, mode) -> this.onlineMode = mode));

        this.pvpEnabledButton = this.addRenderableWidget(
                CycleButton.onOffBuilder(this.pvpEnabled).create(cx + 5, 114, 150, 20,
                        PVP_ENABLED_TEXT, (button, enabled) -> this.pvpEnabled = enabled));

        this.tunnelButton = this.addRenderableWidget(
                CycleButton.builder(TunnelType::getTranslatableName).withValues(TunnelType.values())
                        .withInitialValue(tunnel).create(cx - 155, 138, 310, 20,
                                TUNNEL_TEXT, (button, t) -> this.tunnel = t));

        this.explanationText = new MultiLineTextWidget(cx - 154, 162, EXPLANATION_TEXT, this.font);
        this.explanationText.setMaxWidth(308);
        this.explanationText.setColor(0xA0A0A0);
        this.addRenderableWidget(this.explanationText);

        this.getPortEdit().setPosition(cx - 154, this.height - 76);
        this.getPortEdit().setWidth(148);
        this.getPortEdit().setValue(this.rawPort != -1 ? Integer.toString(this.getPort()) : "");

        this.maxPlayersField = new EditBox(this.font, cx + 6, this.height - 76, 148, 20, MAX_PLAYERS_TEXT);
        this.maxPlayersField.setValue(Integer.toString(maxPlayers));
        this.maxPlayersField.setFilter(s -> {
            if (s.isEmpty()) {
                this.maxPlayersValid = false;
                this.maxPlayersField.setTextColor(0xFF5555);
                this.maxPlayersField.setTooltip(Tooltip.create(INVALID_MAX_PLAYERS_TEXT));
                if (this.startSaveButton != null) this.startSaveButton.active = this.canStartOrSave();
                return true;
            }
            try {
                int v = Integer.parseInt(s);
                if (v > 0) {
                    this.maxPlayers = v;
                    this.maxPlayersValid = true;
                    this.maxPlayersField.setTextColor(0xFFFFFF);
                    this.maxPlayersField.setTooltip(null);
                    if (this.startSaveButton != null) this.startSaveButton.active = this.canStartOrSave();
                    return true;
                }
            } catch (NumberFormatException e) {
            }
            this.maxPlayersValid = false;
            this.maxPlayersField.setTextColor(0xFF5555);
            this.maxPlayersField.setTooltip(Tooltip.create(INVALID_MAX_PLAYERS_TEXT));
            if (this.startSaveButton != null) this.startSaveButton.active = this.canStartOrSave();
            return false;
        });
        this.addRenderableWidget(this.maxPlayersField);

        this.motdField = new EditBox(this.font, cx - 154, this.height - 52, 308, 20, MOTD_TEXT);
        this.motdField.setMaxLength(59);
        this.motdField.setValue(this.rawMotd);
        this.motdField.setTooltip(Tooltip.create(Component.literal(
                LanServer.processMotd(server, MOTD_DESCRIPTION_TEXT.getString()))));
        this.motdField.setFilter(s -> { this.rawMotd = s; return true; });
        this.addRenderableWidget(this.motdField);

        Component startLabel = Component.translatable("lanServer.start");
        for (var child : this.children()) {
            if (child instanceof Button button && button.getMessage().equals(startLabel)) {
                this.removeWidget(child);
                break;
            }
        }
        this.startSaveButton = this.addRenderableWidget(
                Button.builder(alreadyPublished ? SAVE_TEXT : START_TEXT, button -> this.lanStartOrSave())
                        .bounds(cx - 155, this.height - 28, alreadyPublished ? 73 : 150, 20).build());
        this.startSaveButton.active = this.canStartOrSave();

        if (alreadyPublished) {
            this.addRenderableWidget(Button.builder(STOP_TEXT, button -> this.lanStop())
                    .bounds(cx - 78, this.height - 28, 73, 20).build());
        }

        Component cancelLabel = CommonComponents.GUI_CANCEL;
        for (var child : this.children()) {
            if (child instanceof Button button && button.getMessage().equals(cancelLabel)) {
                this.removeWidget(button);
                this.addRenderableWidget(button);
                break;
            }
        }
    }

    @Overwrite
    // 描画
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        int cx = this.width / 2;
        guiGraphics.drawString(this.font, PER_WORLD_TEXT, cx - 155, 14, 0xFFFFFF);
        guiGraphics.drawString(this.font, GLOBAL_TEXT, cx - 155, 36, 0xFFFFFF);
        guiGraphics.drawString(this.font, SYSTEM_TEXT, cx - 155, 58, 0xFFFFFF);
        guiGraphics.drawString(this.font, PORT_TEXT, cx - 154, this.height - 88, 0xA0A0A0);
        guiGraphics.drawString(this.font, MAX_PLAYERS_TEXT, cx + 6, this.height - 88, 0xA0A0A0);
    }

    @Unique
    // LAN開始または保存
    private void lanStartOrSave() {
        this.minecraft.setScreen(null);
        LanServer.startOrSaveLan(Minecraft.getInstance().getSingleplayerServer(),
                this.getGameMode(), this.onlineMode, this.pvpEnabled, this.tunnel,
                this.getPort(), this.maxPlayers, this.rawMotd,
                text -> Minecraft.getInstance().gui.getChat().addMessage(text),
                () -> Minecraft.getInstance().gui.getChat().addMessage(
                        Component.translatable("commands.publish.failed")),
                text -> Minecraft.getInstance().gui.getChat().addMessage(text));
    }

    @Unique
    // LAN停止
    private void lanStop() {
        this.minecraft.setScreen(null);
        LanServer.stopLan(Minecraft.getInstance().getSingleplayerServer(),
                () -> Minecraft.getInstance().gui.getChat().addMessage(
                        Component.translatable("commands.publish.stopped")),
                () -> Minecraft.getInstance().gui.getChat().addMessage(
                        Component.translatable("commands.publish.failed.stop")),
                text -> Minecraft.getInstance().gui.getChat().addMessage(text));
    }

    @Override
    // キー押下処理
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (this.canStartOrSave()) {
                this.lanStartOrSave();
            }
            return true;
        }
        return false;
    }
}
