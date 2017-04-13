module.exports = function(config) {
    config.set({
        basePath: './src/main/resources/static',

        files: [
            'lib/angular/angular.js',
            'lib/angular-bootstrap/ui-bootstrap-tpls.js',
            'lib/angular-resource/angular-resource.js',
            'lib/angular-route/angular-route.js',
            'lib/angular-mocks/angular-mocks.js',
            'lib/codemirror/lib/codemirror.js',
            '**/*.module.js',
            '*!(.module|.spec).js',
            '!(lib)/**/*!(.module|.spec).js',
            '**/*.spec.js'
        ],

        autoWatch: true,

        frameworks: ['jasmine'],

        browsers: ['Chrome', 'Firefox'],

        plugins: [
            'karma-chrome-launcher',
            // 'karma-firefox-launcher',
            'karma-jasmine'
        ]
    });
};