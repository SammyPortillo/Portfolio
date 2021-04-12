(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
 (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
 m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
 })(window,document,'script','//www.google-analytics.com/analytics.js','ga');<!-- Create 2 Dimensional Array -->
var myList = [["HEBPRD","UA-31039832-6"]]
 myList.push(["/CEBPRD/", "UA-30788934-13"]); myList.push(["/CEBPRDM/", "UA-30788934-34"]); var myURL = document.URL; for (var i = 0; i < myList.length; i++) {

 if (myURL.indexOf(myList[i][0]) >= 0) {

 ga('create', myList[i][1], 'auto'); }

 }

 ga('send', 'pageview');