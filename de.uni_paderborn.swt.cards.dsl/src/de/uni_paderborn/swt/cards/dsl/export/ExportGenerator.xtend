package de.uni_paderborn.swt.cards.dsl.export

import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model
import java.util.List
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation
import java.util.ArrayList

class ExportGenerator {
	
	Model model;
	List<ComponentResult> analysisResults;
	List<RestrictionViolation> violations;
	
	new (Model m, List<ComponentResult> analysisResults, List<RestrictionViolation> violations) {
		this.model = m;
		this.analysisResults = analysisResults;
		this.violations = new ArrayList(violations.toSet);
	}
	
	def generate() {
		'''
		<html>
		  <head>
		  	<style>
		  		«generateCSS»
		  	</style>
		  </head>
		  <body>
		      <div>
		        <ul class="pl-4 py-2 text-xl text-white flex space-x-4 bg-gray-800">
		          <li id="RestrictionButton"class="hover:bg-gray-700 py-1 px-2 rounded-lg" >
		            <a href="#" onclick="return show('Restrictions', 'RestrictionButton');">Restrictions</a>
		          </li>
		          <li id="AssumptionButton" class="hover:bg-gray-700 py-1 px-2 rounded-lg">
		            <a href="#" onclick="return show('Assumptions', 'AssumptionButton');">Assumptions</a>
		          </li>
		          <li id="ViolationButton" class="hover:bg-gray-700 py-1 px-2 rounded-lg">
		              <a href="#" onclick="return show('Violation', 'ViolationButton');">Restriction Violations</a>
		          </li>
		          <li id="TraceButton" class="hover:bg-gray-700 py-1 px-2 rounded-lg">
		              <a href="#" onclick="return show('Trace', 'TraceButton');">Trace</a>
		          </li>
		        </ul>
		      </div>
		      «new RestrictionsExporter(model).generate»
		      «new AssumptionsExporter(model).generate»
		      «new ViolationExporter(violations).generate»
		          <div id="Trace" class="hidden">
		              <pre id="traceJSON" class="ml-4 mt-4">
		              </pre>
		          </div>
		  </body>
		</html>
		<script>
		    data=«new TraceExporter(analysisResults).generate»
		    document.getElementById("traceJSON").textContent = JSON.stringify(data, undefined, 2);
		  var coll = document.getElementsByClassName("collapsible");
		  var i;
		
		  for (i = 0; i < coll.length; i++) {
		    coll[i].addEventListener("click", function () {
		      var content = this.nextElementSibling;
		      content.classList.toggle("hidden");
		    });
		  }
		
		  function show(page, button) {
		    document.getElementById("Restrictions").classList.add("hidden");
		    document.getElementById("Assumptions").classList.add("hidden");
		    document.getElementById("Trace").classList.add("hidden");
		    document.getElementById("Violation").classList.add("hidden");
		    document.getElementById("RestrictionButton").classList.remove("bg-gray-700");
		    document.getElementById("AssumptionButton").classList.remove("bg-gray-700");   
		    document.getElementById("TraceButton").classList.remove("bg-gray-700");   
			document.getElementById("ViolationButton").classList.remove("bg-gray-700");  
			
			
		    document.getElementById(page).classList.remove("hidden");
		    document.getElementById(button).classList.add("bg-gray-700");
		    return true;
		  }
		
		  function toggleShow() {
		        var p1 = document.getElementById("Page1");
		        var p2 = document.getElementById("Page2");
		
		        p1.classList.toggle("hidden");
		        p2.classList.toggle("hidden");
		    }
		</script>
		'''
	}
	
	private def generateCSS() {
		'''
/*! normalize.css v8.0.1 | MIT License | github.com/necolas/normalize.css */

/* Document
   ========================================================================== */

/**
 * 1. Correct the line height in all browsers.
 * 2. Prevent adjustments of font size after orientation changes in iOS.
 */

html {
  line-height: 1.15; /* 1 */
  -webkit-text-size-adjust: 100%; /* 2 */
}

/* Sections
   ========================================================================== */

/**
 * Remove the margin in all browsers.
 */

body {
  margin: 0;
}

/**
 * Render the `main` element consistently in IE.
 */

main {
  display: block;
}

/**
 * Correct the font size and margin on `h1` elements within `section` and
 * `article` contexts in Chrome, Firefox, and Safari.
 */

h1 {
  font-size: 2em;
  margin: 0.67em 0;
}

/* Grouping content
   ========================================================================== */

/**
 * 1. Add the correct box sizing in Firefox.
 * 2. Show the overflow in Edge and IE.
 */

hr {
  box-sizing: content-box; /* 1 */
  height: 0; /* 1 */
  overflow: visible; /* 2 */
}

/**
 * 1. Correct the inheritance and scaling of font size in all browsers.
 * 2. Correct the odd `em` font sizing in all browsers.
 */

pre {
  font-family: monospace, monospace; /* 1 */
  font-size: 1em; /* 2 */
}

/* Text-level semantics
   ========================================================================== */

/**
 * Remove the gray background on active links in IE 10.
 */

a {
  background-color: transparent;
}

/**
 * 1. Remove the bottom border in Chrome 57-
 * 2. Add the correct text decoration in Chrome, Edge, IE, Opera, and Safari.
 */

abbr[title] {
  border-bottom: none; /* 1 */
  text-decoration: underline; /* 2 */
  -webkit-text-decoration: underline dotted;
          text-decoration: underline dotted; /* 2 */
}

/**
 * Add the correct font weight in Chrome, Edge, and Safari.
 */

b,
strong {
  font-weight: bolder;
}

/**
 * 1. Correct the inheritance and scaling of font size in all browsers.
 * 2. Correct the odd `em` font sizing in all browsers.
 */

code,
kbd,
samp {
  font-family: monospace, monospace; /* 1 */
  font-size: 1em; /* 2 */
}

/**
 * Add the correct font size in all browsers.
 */

small {
  font-size: 80%;
}

/**
 * Prevent `sub` and `sup` elements from affecting the line height in
 * all browsers.
 */

sub,
sup {
  font-size: 75%;
  line-height: 0;
  position: relative;
  vertical-align: baseline;
}

sub {
  bottom: -0.25em;
}

sup {
  top: -0.5em;
}

/* Embedded content
   ========================================================================== */

/**
 * Remove the border on images inside links in IE 10.
 */

img {
  border-style: none;
}

/* Forms
   ========================================================================== */

/**
 * 1. Change the font styles in all browsers.
 * 2. Remove the margin in Firefox and Safari.
 */

button,
input,
optgroup,
select,
textarea {
  font-family: inherit; /* 1 */
  font-size: 100%; /* 1 */
  line-height: 1.15; /* 1 */
  margin: 0; /* 2 */
}

/**
 * Show the overflow in IE.
 * 1. Show the overflow in Edge.
 */

button,
input { /* 1 */
  overflow: visible;
}

/**
 * Remove the inheritance of text transform in Edge, Firefox, and IE.
 * 1. Remove the inheritance of text transform in Firefox.
 */

button,
select { /* 1 */
  text-transform: none;
}

/**
 * Correct the inability to style clickable types in iOS and Safari.
 */

button,
[type="button"],
[type="reset"],
[type="submit"] {
  -webkit-appearance: button;
}

/**
 * Remove the inner border and padding in Firefox.
 */

button::-moz-focus-inner,
[type="button"]::-moz-focus-inner,
[type="reset"]::-moz-focus-inner,
[type="submit"]::-moz-focus-inner {
  border-style: none;
  padding: 0;
}

/**
 * Restore the focus styles unset by the previous rule.
 */

button:-moz-focusring,
[type="button"]:-moz-focusring,
[type="reset"]:-moz-focusring,
[type="submit"]:-moz-focusring {
  outline: 1px dotted ButtonText;
}

/**
 * Correct the padding in Firefox.
 */

fieldset {
  padding: 0.35em 0.75em 0.625em;
}

/**
 * 1. Correct the text wrapping in Edge and IE.
 * 2. Correct the color inheritance from `fieldset` elements in IE.
 * 3. Remove the padding so developers are not caught out when they zero out
 *    `fieldset` elements in all browsers.
 */

legend {
  box-sizing: border-box; /* 1 */
  color: inherit; /* 2 */
  display: table; /* 1 */
  max-width: 100%; /* 1 */
  padding: 0; /* 3 */
  white-space: normal; /* 1 */
}

/**
 * Add the correct vertical alignment in Chrome, Firefox, and Opera.
 */

progress {
  vertical-align: baseline;
}

/**
 * Remove the default vertical scrollbar in IE 10+.
 */

textarea {
  overflow: auto;
}

/**
 * 1. Add the correct box sizing in IE 10.
 * 2. Remove the padding in IE 10.
 */

[type="checkbox"],
[type="radio"] {
  box-sizing: border-box; /* 1 */
  padding: 0; /* 2 */
}

/**
 * Correct the cursor style of increment and decrement buttons in Chrome.
 */

[type="number"]::-webkit-inner-spin-button,
[type="number"]::-webkit-outer-spin-button {
  height: auto;
}

/**
 * 1. Correct the odd appearance in Chrome and Safari.
 * 2. Correct the outline style in Safari.
 */

[type="search"] {
  -webkit-appearance: textfield; /* 1 */
  outline-offset: -2px; /* 2 */
}

/**
 * Remove the inner padding in Chrome and Safari on macOS.
 */

[type="search"]::-webkit-search-decoration {
  -webkit-appearance: none;
}

/**
 * 1. Correct the inability to style clickable types in iOS and Safari.
 * 2. Change font properties to `inherit` in Safari.
 */

::-webkit-file-upload-button {
  -webkit-appearance: button; /* 1 */
  font: inherit; /* 2 */
}

/* Interactive
   ========================================================================== */

/*
 * Add the correct display in Edge, IE 10+, and Firefox.
 */

details {
  display: block;
}

/*
 * Add the correct display in all browsers.
 */

summary {
  display: list-item;
}

/* Misc
   ========================================================================== */

/**
 * Add the correct display in IE 10+.
 */

template {
  display: none;
}

/**
 * Add the correct display in IE 10.
 */

[hidden] {
  display: none;
}

/**
 * Manually forked from SUIT CSS Base: https://github.com/suitcss/base
 * A thin layer on top of normalize.css that provides a starting point more
 * suitable for web applications.
 */

/**
 * Removes the default spacing and border for appropriate elements.
 */

blockquote,
dl,
dd,
h1,
h2,
h3,
h4,
h5,
h6,
hr,
figure,
p,
pre {
  margin: 0;
}

button {
  background-color: transparent;
  background-image: none;
  padding: 0;
}

/**
 * Work around a Firefox/IE bug where the transparent `button` background
 * results in a loss of the default `button` focus styles.
 */

button:focus {
  outline: 1px dotted;
  outline: 5px auto -webkit-focus-ring-color;
}

fieldset {
  margin: 0;
  padding: 0;
}

ol,
ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

/**
 * Tailwind custom reset styles
 */

/**
 * 1. Use the user's configured `sans` font-family (with Tailwind's default
 *    sans-serif font stack as a fallback) as a sane default.
 * 2. Use Tailwind's default "normal" line-height so the user isn't forced
 *    to override it to ensure consistency even when using the default theme.
 */

html {
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji"; /* 1 */
  line-height: 1.5; /* 2 */
}

/**
 * 1. Prevent padding and border from affecting element width.
 *
 *    We used to set this in the html element and inherit from
 *    the parent element for everything else. This caused issues
 *    in shadow-dom-enhanced elements like <details> where the content
 *    is wrapped by a div with box-sizing set to `content-box`.
 *
 *    https://github.com/mozdevs/cssremedy/issues/4
 *
 *
 * 2. Allow adding a border to an element by just adding a border-width.
 *
 *    By default, the way the browser specifies that an element should have no
 *    border is by setting it's border-style to `none` in the user-agent
 *    stylesheet.
 *
 *    In order to easily add borders to elements by just setting the `border-width`
 *    property, we change the default border-style for all elements to `solid`, and
 *    use border-width to hide them instead. This way our `border` utilities only
 *    need to set the `border-width` property instead of the entire `border`
 *    shorthand, making our border utilities much more straightforward to compose.
 *
 *    https://github.com/tailwindcss/tailwindcss/pull/116
 */

*,
::before,
::after {
  box-sizing: border-box; /* 1 */
  border-width: 0; /* 2 */
  border-style: solid; /* 2 */
  border-color: #e2e8f0; /* 2 */
}

/*
 * Ensure horizontal rules are visible by default
 */

hr {
  border-top-width: 1px;
}

/**
 * Undo the `border-style: none` reset that Normalize applies to images so that
 * our `border-{width}` utilities have the expected effect.
 *
 * The Normalize reset is unnecessary for us since we default the border-width
 * to 0 on all elements.
 *
 * https://github.com/tailwindcss/tailwindcss/issues/362
 */

img {
  border-style: solid;
}

textarea {
  resize: vertical;
}

input::-moz-placeholder, textarea::-moz-placeholder {
  color: #a0aec0;
}

input:-ms-input-placeholder, textarea:-ms-input-placeholder {
  color: #a0aec0;
}

input::-ms-input-placeholder, textarea::-ms-input-placeholder {
  color: #a0aec0;
}

input::placeholder,
textarea::placeholder {
  color: #a0aec0;
}

button,
[role="button"] {
  cursor: pointer;
}

table {
  border-collapse: collapse;
}

h1,
h2,
h3,
h4,
h5,
h6 {
  font-size: inherit;
  font-weight: inherit;
}

/**
 * Reset links to optimize for opt-in styling instead of
 * opt-out.
 */

a {
  color: inherit;
  text-decoration: inherit;
}

/**
 * Reset form element properties that are easy to forget to
 * style explicitly so you don't inadvertently introduce
 * styles that deviate from your design system. These styles
 * supplement a partial reset that is already applied by
 * normalize.css.
 */

button,
input,
optgroup,
select,
textarea {
  padding: 0;
  line-height: inherit;
  color: inherit;
}

/**
 * Use the configured 'mono' font family for elements that
 * are expected to be rendered with a monospace font, falling
 * back to the system monospace stack if there is no configured
 * 'mono' font family.
 */

pre,
code,
kbd,
samp {
  font-family: Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

/**
 * Make replaced elements `display: block` by default as that's
 * the behavior you want almost all of the time. Inspired by
 * CSS Remedy, with `svg` added as well.
 *
 * https://github.com/mozdevs/cssremedy/issues/14
 */

img,
svg,
video,
canvas,
audio,
iframe,
embed,
object {
  display: block;
  vertical-align: middle;
}

/**
 * Constrain images and videos to the parent width and preserve
 * their instrinsic aspect ratio.
 *
 * https://github.com/mozdevs/cssremedy/issues/14
 */

img,
video {
  max-width: 100%;
  height: auto;
}

/*tailwind start components */

.container {
  width: 100%;
}

@media (min-width: 640px) {
  .container {
    max-width: 640px;
  }
}

@media (min-width: 768px) {
  .container {
    max-width: 768px;
  }
}

@media (min-width: 1024px) {
  .container {
    max-width: 1024px;
  }
}

@media (min-width: 1280px) {
  .container {
    max-width: 1280px;
  }
}

/*tailwind end components */

.space-x-4 > :not(template) ~ :not(template) {
  --space-x-reverse: 0;
  margin-right: calc(1rem * var(--space-x-reverse));
  margin-left: calc(1rem * calc(1 - var(--space-x-reverse)));
}

.bg-gray-100 {
  --bg-opacity: 1;
  background-color: #f7fafc;
  background-color: rgba(247, 250, 252, var(--bg-opacity));
}

.bg-gray-700 {
  --bg-opacity: 1;
  background-color: #4a5568;
  background-color: rgba(74, 85, 104, var(--bg-opacity));
}

.bg-gray-800 {
  --bg-opacity: 1;
  background-color: #2d3748;
  background-color: rgba(45, 55, 72, var(--bg-opacity));
}

.bg-red-300 {
  --bg-opacity: 1;
  background-color: #feb2b2;
  background-color: rgba(254, 178, 178, var(--bg-opacity));
}

.bg-orange-300 {
  --bg-opacity: 1;
  background-color: #fbd38d;
  background-color: rgba(251, 211, 141, var(--bg-opacity));
}

.bg-green-200 {
  --bg-opacity: 1;
  background-color: #c6f6d5;
  background-color: rgba(198, 246, 213, var(--bg-opacity));
}

.bg-blue-200 {
  --bg-opacity: 1;
  background-color: #bee3f8;
  background-color: rgba(190, 227, 248, var(--bg-opacity));
}

.bg-blue-300 {
  --bg-opacity: 1;
  background-color: #90cdf4;
  background-color: rgba(144, 205, 244, var(--bg-opacity));
}

.hover\:bg-gray-700:hover {
  --bg-opacity: 1;
  background-color: #4a5568;
  background-color: rgba(74, 85, 104, var(--bg-opacity));
}

.hover\:bg-blue-200:hover {
  --bg-opacity: 1;
  background-color: #bee3f8;
  background-color: rgba(190, 227, 248, var(--bg-opacity));
}

.hover\:bg-blue-400:hover {
  --bg-opacity: 1;
  background-color: #63b3ed;
  background-color: rgba(99, 179, 237, var(--bg-opacity));
}

.border-collapse {
  border-collapse: collapse;
}

.rounded-md {
  border-radius: 0.375rem;
}

.rounded-lg {
  border-radius: 0.5rem;
}

.border {
  border-width: 1px;
}

.block {
  display: block;
}

.flex {
  display: flex;
}

.table {
  display: table;
}

.hidden {
  display: none;
}

.flex-wrap {
  flex-wrap: wrap;
}

.items-center {
  align-items: center;
}

.justify-center {
  justify-content: center;
}

.flex-auto {
  flex: 1 1 auto;
}

.font-semibold {
  font-weight: 600;
}

.text-xl {
  font-size: 1.25rem;
}

.text-2xl {
  font-size: 1.5rem;
}

.m-2 {
  margin: 0.5rem;
}

.my-3 {
  margin-top: 0.75rem;
  margin-bottom: 0.75rem;
}

.mr-1 {
  margin-right: 0.25rem;
}

.mt-2 {
  margin-top: 0.5rem;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.mt-4 {
  margin-top: 1rem;
}

.ml-4 {
  margin-left: 1rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.max-w-4xl {
  max-width: 56rem;
}

.p-2 {
  padding: 0.5rem;
}

.py-1 {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
}

.py-2 {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.px-2 {
  padding-left: 0.5rem;
  padding-right: 0.5rem;
}

.py-6 {
  padding-top: 1.5rem;
  padding-bottom: 1.5rem;
}

.pb-2 {
  padding-bottom: 0.5rem;
}

.pl-4 {
  padding-left: 1rem;
}

.pl-8 {
  padding-left: 2rem;
}

.resize {
  resize: both;
}

.shadow-2xl {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.text-center {
  text-align: center;
}

.text-white {
  --text-opacity: 1;
  color: #fff;
  color: rgba(255, 255, 255, var(--text-opacity));
}

.underline {
  text-decoration: underline;
}

.visible {
  visibility: visible;
}

.break-words {
  overflow-wrap: break-word;
}

.w-1\/6 {
  width: 16.666667%;
}

.w-4\/6 {
  width: 66.666667%;
}

.w-full {
  width: 100%;
}

.transform {
  --transform-translate-x: 0;
  --transform-translate-y: 0;
  --transform-rotate: 0;
  --transform-skew-x: 0;
  --transform-skew-y: 0;
  --transform-scale-x: 1;
  --transform-scale-y: 1;
  transform: translateX(var(--transform-translate-x)) translateY(var(--transform-translate-y)) rotate(var(--transform-rotate)) skewX(var(--transform-skew-x)) skewY(var(--transform-skew-y)) scaleX(var(--transform-scale-x)) scaleY(var(--transform-scale-y));
}
		'''
	}
}