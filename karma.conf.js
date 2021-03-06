module.exports = function(config) {
    config.set({
        basePath: './src/webapp',

        files: [
            'lib/angular/angular.js',
            'lib/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
            'lib/angular-resource/angular-resource.js',
            'lib/angular-route/angular-route.js',
            'lib/angular-mocks/angular-mocks.js',
            'lib/codemirror/lib/codemirror.js',
            '!(lib)/**/*.module.js',
            '!(lib)/**/*!(.module|.spec).js',
            '!(lib)/**/*.spec.js'
        ],

        autoWatch: true,

        frameworks: ['jasmine'],

        browsers: ['Chrome', 'Firefox'],

        plugins: [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-junit-reporter',
            'karma-jasmine'
        ]
    });
};