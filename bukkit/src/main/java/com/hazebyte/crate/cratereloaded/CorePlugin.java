package com.hazebyte.crate.cratereloaded;

import static com.hazebyte.crate.cratereloaded.util.ConfigConstants.CONFIG_FILE_NAME;

import co.aikar.commands.BukkitCommandManager;
import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.CratePlugin;
import com.hazebyte.crate.api.ServerVersion;
import com.hazebyte.crate.api.claim.ClaimRegistrar;
import com.hazebyte.crate.api.crate.BlockCrateRegistrar;
import com.hazebyte.crate.api.crate.CrateRegistrar;
import com.hazebyte.crate.api.event.PluginReadyEvent;
import com.hazebyte.crate.api.util.Messenger;
import com.hazebyte.crate.cratereloaded.claim.ClaimManager;
import com.hazebyte.crate.cratereloaded.cmd.CrateCommandManager;
import com.hazebyte.crate.cratereloaded.component.impl.FilePluginSettingComponentImpl;
import com.hazebyte.crate.cratereloaded.crate.BlockCrateHandler;
import com.hazebyte.crate.cratereloaded.crate.CrateHandler;
import com.hazebyte.crate.cratereloaded.dagger.DaggerJavaPluginComponent;
import com.hazebyte.crate.cratereloaded.dagger.JavaPluginComponent;
import com.hazebyte.crate.cratereloaded.listener.ListenerManager;
import com.hazebyte.crate.cratereloaded.listener.original.WorldLoadListener;
import com.hazebyte.crate.cratereloaded.locale.Locales;
import com.hazebyte.crate.cratereloaded.metric.CrateMetrics;
import com.hazebyte.crate.cratereloaded.model.Config;
import com.hazebyte.crate.cratereloaded.model.mapper.CrateMapper;
import com.hazebyte.crate.cratereloaded.model.mapper.RewardMapper;
import com.hazebyte.crate.cratereloaded.provider.EconomyProviderSelector;
import com.hazebyte.crate.cratereloaded.provider.HologramProviderSelector;
import com.hazebyte.crate.cratereloaded.provider.Provider;
import com.hazebyte.crate.cratereloaded.provider.ProviderSelector;
import com.hazebyte.crate.cratereloaded.provider.economy.EconomyProvider;
import com.hazebyte.crate.cratereloaded.provider.holographic.HologramProvider;
import com.hazebyte.crate.cratereloaded.provider.holographic.HologramWrapper;
import com.hazebyte.crate.exception.ExceptionHandler;
import com.hazebyte.crate.exception.items.DependencyNotEnabledException;
import com.hazebyte.crate.exception.items.InvalidInputException;
import com.hazebyte.crate.logger.JSONFormatter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.mapstruct.factory.Mappers;

/**
 * The plugin's central core that handles individual parts of the plugin. Allows for static calls
 * through the instance of the plugin.
 * 
 * <p>This class serves as the main entry point for the CrateReloaded plugin. It initializes all
 * necessary components, manages the plugin lifecycle, and provides access to various plugin
 * functionalities through its methods. It implements the CratePlugin interface to provide
 * standardized access to plugin features.</p>
 *
 * <p>The CorePlugin class is responsible for:</p>
 * <ul>
 *   <li>Plugin initialization and shutdown</li>
 *   <li>Component registration (economy, holographics, etc.)</li>
 *   <li>Configuration and settings management</li>
 *   <li>Providing access to plugin subsystems</li>
 *   <li>Handling plugin events and interactions</li>
 * </ul>
 *
 * @author William
 */
public class CorePlugin extends JavaPlugin implements CratePlugin {
    /** Metadata placeholder for Spigot user identification */
    public static String SPIGOT_USER_META = "%%__USER__%%";
    
    /** Metadata placeholder for Spigot resource identification */
    public static String SPIGOT_RESOURCE_META = "%%__RESOURCE__%%";
    
    /** Metadata placeholder for Spigot download nonce */
    public static String SPIGOT_DOWNLOAD_META = "%%__NONCE__%%";
    
    /** Static instance of the plugin for global access */
    private static CorePlugin plugin;
    
    /** Command manager for handling plugin commands */
    private static BukkitCommandManager commandHandler;
    
    /** Dagger component for dependency injection */
    private static JavaPluginComponent javaPluginComponent;

    /** Handler for managing exceptions throughout the plugin */
    private ExceptionHandler exceptionHandler;
    
    /** Handler for managing crates and their functionality */
    private CrateHandler crateHandler;
    
    /** Manager for handling claim-related operations */
    private ClaimManager claimManager;
    
    /** Handler for managing block-based crates */
    private BlockCrateHandler blockCrateHandler;
    
    /** Manager for plugin event listeners */
    private ListenerManager listenerHandler;
    
    /** Provider for holographic displays */
    private HologramProvider<? extends HologramWrapper> hologramProvider;
    
    /** Provider for economy integration */
    private EconomyProvider<? extends EconomyResponse> economyProvider;
    
    /** Current server version information */
    private ServerVersion serverVersion;
    
    /** Localization manager for plugin messages */
    private Locales locale;
    
    /** Metrics collector for plugin usage statistics */
    private Metrics metrics;

    /** Component for accessing plugin settings from configuration */
    private FilePluginSettingComponentImpl settings;

    /** Mapper for converting between crate models */
    public static final CrateMapper CRATE_MAPPER = Mappers.getMapper(CrateMapper.class);
    
    /** Mapper for converting between reward models */
    public static final RewardMapper REWARD_MAPPER = Mappers.getMapper(RewardMapper.class);
    
    /** Flag indicating whether the plugin is fully initialized and ready */
    private boolean isReady = false;

    /**
     * Default constructor for the plugin.
     * Used by Bukkit when loading the plugin normally.
     */
    public CorePlugin() {
        super();
    }

    /**
     * Constructor used for testing purposes.
     * Allows for mock plugin initialization with custom parameters.
     *
     * @param loader The JavaPluginLoader instance
     * @param description The plugin description file
     * @param dataFolder The plugin's data folder
     * @param file The plugin's jar file
     */
    protected CorePlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * Gets the singleton instance of the plugin.
     *
     * @return The CorePlugin instance
     * @deprecated Use dependency injection instead. This static accessor will be removed in v3.0.0.
     *             Static access creates coupling and can cause NPEs during plugin reload.
     */
    @Deprecated
    public static CorePlugin getPlugin() {
        if (plugin == null) {
            throw new IllegalStateException(
                    "CorePlugin instance is null - plugin may be disabled or not yet loaded. "
                            + "Avoid static access and use dependency injection instead.");
        }
        return plugin;
    }

    /**
     * Gets the JavaPluginComponent for dependency injection.
     *
     * @return The JavaPluginComponent instance
     * @deprecated Use dependency injection instead. This static accessor will be removed in v3.0.0.
     *             Inject JavaPluginComponent or specific dependencies directly via Dagger.
     */
    @Deprecated
    public static JavaPluginComponent getJavaPluginComponent() {
        if (javaPluginComponent == null) {
            throw new IllegalStateException(
                    "JavaPluginComponent is null - plugin may be disabled or not yet initialized. "
                            + "Avoid static access and use dependency injection instead.");
        }
        return javaPluginComponent;
    }

    /**
     * Reloads the crates and locale internally without reloading the plugin.
     * This method disables and then re-enables the plugin's components to refresh configurations.
     * The settings cache is cleared on re-enable to ensure fresh config values are loaded.
     */
    public void reloadAll() {
        // Clear settings cache before reload to prevent stale values
        if (settings != null) {
            settings.clearCache();
        }
        onDisable();
        onEnable();
    }

    /** 
     * Registers the locale manager and loads language files.
     * This initializes the localization system for the plugin.
     */
    private void registerLocale() {
        locale = new Locales(this);
        locale.loadLanguages();
    }

    /**
     * Gets the holographic display provider.
     *
     * @return The current hologram provider
     */
    public HologramProvider<? extends HologramWrapper> getHolographicProvider() {
        return hologramProvider;
    }

    /**
     * Gets a localized message for the specified key.
     * If running on a mock server, returns the key itself.
     *
     * @param key The message key to look up
     * @return The localized message string
     */
    @Override
    public String getMessage(String key) {
        if (serverVersion.isMockServer()) {
            return key;
        }
        return locale.getMessage(key);
    }

    /**
     * Obtains the plugin's JAR file.
     * This overrides the protected method in JavaPlugin to make it public.
     *
     * @return File object representing the plugin's JAR file
     */
    public File getFile() {
        return super.getFile();
    }

    /**
     * Starts the cleanup process when the plugin is disabled.
     * 
     * <p>This method performs the following cleanup operations:</p>
     * <ul>
     *   <li>Clears world load listeners</li>
     *   <li>Disposes effect services</li>
     *   <li>Removes all holograms</li>
     *   <li>Unregisters commands</li>
     *   <li>Closes and removes logger handlers</li>
     *   <li>Unregisters all event listeners</li>
     *   <li>Nullifies component references</li>
     *   <li>Cancels all scheduled tasks</li>
     * </ul>
     */
    public void onDisable() {
        // Clear world load listeners
        WorldLoadListener.clear();
        
        // Dispose effect services
        if (javaPluginComponent != null) {
            javaPluginComponent.getEffectServiceComponent().dispose();
        }
        
        // Remove all holograms
        if (hologramProvider != null) {
            hologramProvider.removeAll();
        }
        
        // Unregister commands
        if (commandHandler != null) {
            commandHandler.unregisterCommands();
        }
        
        // Close and remove logger handlers
        for (Handler handler : this.getLogger().getHandlers()) {
            handler.close();
            this.getLogger().removeHandler(handler);
        }
        
        // Unregister all event listeners
        HandlerList.unregisterAll(this);

        // Nullify component references
        settings = null;
        locale = null;
        crateHandler = null;
        commandHandler = null;
        listenerHandler = null;
        economyProvider = null;
        hologramProvider = null;
        
        // Cancel all scheduled tasks
        Bukkit.getScheduler().cancelTasks(this);

        // Nullify remaining references
        // WARNING: Setting static references to null can cause NPEs in async tasks
        // that are still running. The @Deprecated getters now throw IllegalStateException
        // to fail-fast instead of silently returning null.
        metrics = null;
        plugin = null;
        javaPluginComponent = null;
        commandHandler = null;
        isReady = false;
    }

    /**
     * Initializes the plugin when it is enabled.
     * 
     * <p>This method performs the following initialization steps:</p>
     * <ul>
     *   <li>Sets up the plugin instance and API implementation</li>
     *   <li>Loads configuration settings</li>
     *   <li>Initializes the exception handler</li>
     *   <li>Sets up dependency injection</li>
     *   <li>Configures the messenger system</li>
     *   <li>Registers the logger</li>
     *   <li>Initializes the claim manager</li>
     *   <li>Registers localization if not in mock mode</li>
     *   <li>Sets up metrics collection if not in mock mode</li>
     *   <li>Registers holographic displays</li>
     *   <li>Registers economy integration</li>
     *   <li>Initializes crate handlers</li>
     *   <li>Sets up event listeners</li>
     *   <li>Initializes command handlers</li>
     *   <li>Marks the plugin as ready</li>
     * </ul>
     */
    public void onEnable() {
        // Set up plugin instance and API implementation
        plugin = this;
        CrateAPI.setImplementation(this);

        Messenger.info("Spigot Version: " + getServerVersion());

        try {
            // Set up dependency injection
            javaPluginComponent = DaggerJavaPluginComponent.create();

            // Initialize components from DI
            exceptionHandler = javaPluginComponent.getExceptionHandler();
            settings = javaPluginComponent.getFilePluginSettingComponentImpl();

            // Configure messenger system
            Messenger.setup(this);
            Messenger.setPrefix(settings.getPluginMessagePrefix());

            // Register logger for file logging
            registerLogger();

            // Initialize claim manager
            claimManager = new ClaimManager(this, javaPluginComponent.getClaimExecutor(), javaPluginComponent.getPluginSettingComponent());

            // Set up localization and metrics if not in mock mode
            if (!getServerVersion().isMockServer()) {
                registerLocale(); // TODO: Review
                metrics = new CrateMetrics(this);
            }
            
            // Register service providers
            registerHolographics();
            registerEconomy();

            // Initialize handlers via DI
            crateHandler = javaPluginComponent.getCrateHandler();
            blockCrateHandler = javaPluginComponent.getBlockCrateHandler();
            listenerHandler = javaPluginComponent.getListenerManager();

            // Set up command handler
            commandHandler = new CrateCommandManager(this);
            
            // Mark plugin as ready
            ready();
        } catch (Exception ex) {
            // Ensure plugin state reflects the failure
            isReady = false;

            // Log critical failure with context
            getLogger().severe("==========================================");
            getLogger().severe("CRITICAL: Failed to initialize CrateReloaded");
            getLogger().severe("The plugin will be disabled to prevent broken operations");
            getLogger().severe("Error: " + ex.getMessage());
            getLogger().severe("==========================================");

            // Log full stack trace for debugging
            exceptionHandler.handle(ex);

            // Disable plugin to prevent NPEs and broken state operations
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Marks the plugin as ready and fires the PluginReadyEvent.
     * This signals to other plugins that CrateReloaded is fully initialized.
     */
    public void ready() {
        // Create and fire the plugin ready event
        PluginReadyEvent event = new PluginReadyEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);

        // Mark the plugin as ready
        isReady = true;
    }

    /**
     * Checks if the plugin is fully initialized and ready.
     *
     * @return true if the plugin is ready, false otherwise
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Returns a string representation of the plugin.
     * Includes the plugin name, version, and primary author.
     *
     * @return A formatted string with plugin information
     */
    public String toString() {
        return String.format(
                "%s %s by %s",
                this.getDescription().getName(),
                this.getDescription().getVersion(),
                this.getDescription().getAuthors().get(0));
    }

    /**
     * Gets the crate registrar for managing crates.
     *
     * @return The CrateRegistrar instance
     */
    public CrateRegistrar getCrateRegistrar() {
        return crateHandler;
    }

    /**
     * Gets the block crate registrar for managing block-based crates.
     *
     * @return The BlockCrateRegistrar instance
     */
    public BlockCrateRegistrar getBlockCrateRegistrar() {
        return blockCrateHandler;
    }

    /**
     * Gets the claim registrar for managing reward claims.
     * Implementation of the CratePlugin interface method.
     *
     * @return The ClaimRegistrar instance
     */
    @Override
    public ClaimRegistrar getClaimRegistrar() {
        return claimManager;
    }

    /**
     * Gets the claim manager instance.
     * This provides direct access to the claim manager implementation.
     *
     * @return The ClaimManager instance
     */
    public ClaimManager getClaimManager() {
        return claimManager;
    }

    /**
     * Gets the server version information.
     * Lazily initializes the server version if not already set.
     *
     * @return The ServerVersion instance
     */
    @Override
    public ServerVersion getServerVersion() {
        if (serverVersion == null) {
            serverVersion = ServerVersion.getVersion();
        }
        return serverVersion;
    }

    /**
     * Returns the Listener Handler class.
     * This provides access to the manager for event listeners.
     *
     * @return The ListenerManager instance
     */
    public ListenerManager getListenerHandler() {
        return listenerHandler;
    }

    /**
     * Gets the economy provider for currency operations.
     *
     * @return The EconomyProvider instance
     */
    public EconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    /**
     * Helper method to get a provider from a selector with error handling.
     * 
     * <p>This method attempts to get a provider based on the specified preference.
     * If the provider cannot be obtained due to dependency issues or invalid input,
     * it falls back to a nil provider. For other reflective operation exceptions,
     * the exception is propagated.</p>
     *
     * @param selector The provider selector to use
     * @param preference The preferred provider name
     * @return The selected provider or a nil provider if there was an error
     * @throws ReflectiveOperationException If there is an error creating the provider
     */
    private Provider getProvider(ProviderSelector selector, String preference) throws ReflectiveOperationException {
        try {
            // Attempt to get the preferred provider
            Provider provider = selector.getProvider(preference);
            if (provider != null) {
                getLogger().info(String.format("Provider: %s", provider.getName()));
            }
            return provider;
        } catch (DependencyNotEnabledException | InvalidInputException ex) {
            // Handle dependency or input errors by falling back to nil provider
            exceptionHandler.handle(ex);
            return selector.getNilProvider();
        } catch (ReflectiveOperationException e) {
            // Propagate other reflective operation exceptions
            throw e;
        }
    }

    /**
     * Registers the holographic display provider.
     * Uses the configured preference to select an appropriate provider.
     *
     * @throws ReflectiveOperationException If there is an error creating the provider
     */
    private void registerHolographics() throws ReflectiveOperationException {
        // Create the hologram provider selector
        ProviderSelector<HologramProvider> hologramProviderSelector = new HologramProviderSelector(this);
        
        // Get the configured preference
        String preference = CorePlugin.getPlugin().getSettings().getHologramPluginPreference();

        // Get the provider based on preference
        hologramProvider = (HologramProvider) getProvider(hologramProviderSelector, preference);
    }

    /**
     * Registers the economy provider.
     * Attempts to use Vault first, falling back to no economy if that fails.
     *
     * @throws ReflectiveOperationException If there is an error creating the provider
     */
    private void registerEconomy() throws ReflectiveOperationException {
        // Create the economy provider selector
        ProviderSelector<EconomyProvider> economyProviderSelector = new EconomyProviderSelector(this);

        try {
            // Try to use Vault as the economy provider
            economyProvider = (EconomyProvider) getProvider(economyProviderSelector, EconomyProviderSelector.VAULT);
        } catch (Exception ex) {
            // Fall back to no economy if Vault fails
            economyProvider = (EconomyProvider) getProvider(economyProviderSelector, EconomyProviderSelector.NONE);
        }
    }

    /**
     * Sets up the file logging system for the plugin.
     * Creates log files with JSON formatting if logging is enabled in settings.
     *
     * @throws IOException If there is an error creating or writing to log files
     */
    private void registerLogger() throws IOException {
        // Skip if logging is disabled in settings
        if (!getSettings().isLogEnabled()) {
            return;
        }

        // Set up JSON formatter for logs
        Formatter formatter = new JSONFormatter();
        
        // Get log level from settings
        Level level = CorePlugin.getPlugin().getSettings().getLogLevel();
        
        // Create date-based log file pattern
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePattern = String.format("%s.%%u.%%g.log", date);
        
        // Create logs directory if it doesn't exist
        File logFolder = new File(plugin.getDataFolder(), "logs");
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }
        
        // Set up file handler with pattern
        String fileHandlerPattern = logFolder.getAbsolutePath() + File.separator + filePattern;
        FileHandler handler = new FileHandler(fileHandlerPattern);
        
        // Configure handler with formatter and level
        handler.setFormatter(formatter);
        handler.setLevel(level);
        
        // Set logger level and add handler
        getLogger().setLevel(level);
        this.getLogger().addHandler(handler);
    }

    /**
     * Gets the exception handler for managing errors.
     *
     * @return The ExceptionHandler instance
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Gets the settings component for accessing plugin configuration.
     *
     * @return The FilePluginSettingComponentImpl instance
     */
    public FilePluginSettingComponentImpl getSettings() {
        return settings;
    }
}
