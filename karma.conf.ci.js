var baseConfig = require('./karma.conf.js');

module.exports = function(config) {
    baseConfig(config);

    config.set({
        singleRun: true,
        autoWatch: false,

        reporters: ['progress', 'junit'],

        junitReporter: {
            outputDir: '../../../../target/surefire-reports',
            suite: 'ui'
        }
    });
};