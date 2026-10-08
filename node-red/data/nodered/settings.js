module.exports = {
    // Define the file for Node-RED flows
    flowFile: 'flows.json',

    // Suppress the welcome dialog in the editor
    editorTheme: {
        projects: {
            enabled: false, // Disable project features
        },
        welcome: {
            enabled: false, // Suppress the welcome dialog
        },
    },

    // Other default settings (you can add more as needed)
    contextStorage: {
        default: {
            module: "memory",
        },
    },

    // Optional: Uncomment the following line to set a custom credential secret
    // credentialSecret: "your-secret-key",

    logging: {
        // Configure the logging level
        console: {
            level: "info",
            metrics: false,
            audit: false,
        },
    },
};

