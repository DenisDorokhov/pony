require.config({
    baseUrl: "js",
    paths: {
        jquery: "lib/jquery",
        json2: "lib/json2",
        underscore: "lib/underscore",
        bootstrap: "lib/bootstrap",
        backbone: "lib/backbone",
        backboneBabySitter: "lib/backbone.babysitter",
        backboneWreqr: "lib/backbone.wreqr",
        marionette: "lib/backbone.marionette"
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
            deps: ["backbone", "backboneBabySitter", "backboneWreqr"],
            exports: "Marionette"
        }
    }
});

require(["marionette"], function() {
    console.log('main');
});