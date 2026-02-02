var isIE = !!document.documentMode;
var isEdge = !isIE && !!window.StyleMedia;
var isFirefox = typeof InstallTrigger !== 'undefined';
var isChrome = navigator.userAgent.includes("Chrome") && navigator.vendor.includes("Google Inc");
var isSupported = isChrome || isEdge || isFirefox;

if (!isSupported) {
	var fallback = document.getElementById("application-fallback");
	var app = document.getElementById("root");

	fallback.style.display = "block";
	app.style.display = "none";
}