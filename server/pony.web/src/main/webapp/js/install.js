require.config({
    baseUrl: "js",
    paths: {
        jquery: "lib/jquery"
    }
});

require(["jquery"], function() {
    console.log("install");
});