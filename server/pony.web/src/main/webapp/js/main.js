require.config({
    baseUrl: "js",
    paths: {
        jquery: "lib/jquery",
        json2: "lib/json2",
        underscore: "lib/underscore",
        bootstrap: "lib/bootstrap",
        backbone: "lib/backbone",
        backboneBabySitter: "lib/backbone.babysitter.js",
        backboneWreqr: "lib/backbone.wreckr.js",
        marionette: "lib/marionette.js"
    },
    shim: {
        bootstrap: {
            deps: ["jquery"]
        },
        backbone: {
            deps: ["jquery", "underscore", "json2"],
            exports: "Backbone"
        },
        marionette: {
            deps: ["backbone", "backboneBabySitter", "backboneWreckr"],
            exports: "Marionette"
        }
    }
});

require(["marionette"], function() {
    console.log('main');
});