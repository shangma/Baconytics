    // Callback that creates and populates a data table,
    // instantiates the pie chart, passes in the data and
    // draws it.
      function drawChart(graphLoc) {

        // Create the data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Topping');
        data.addColumn('number', 'Slices');
        data.addRows([
          ['Mushrooms', 3],
          ['Onions', 1],
          ['Olives', 1],
          ['Zucchini', 1],
          ['Pepperoni', 2]
        ]);

        // Set chart options
        var options = {'title':'How Much Pizza I Ate Last Night',
                       'width':400,
                       'height':300};

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('graphLeft'));
        chart.draw(data, options);
      }
	  
	 
	 // Callback that creates and populates a data table,
    // instantiates the pie chart, passes in the data and
    // draws it.
      function drawChart2() {

        // Create the data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Topping');
        data.addColumn('number', 'Slices');
        data.addRows([
          ['Porkchops', 3],
          ['second', 1],
          ['third', 1],
          ['fourth', 1],
          ['fifth', 2]
        ]);

        // Set chart options
        var options = {'title':'How Much Pizza I Ate Last Night',
                       'width':400,
                       'height':300};

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('graphRight'));
        chart.draw(data, options);
      }
	  
	  function drawChart3() {
		//alert("IN THERE!");
        // Create the data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Topping');
        data.addColumn('number', 'Slices');
        data.addRows([
          ['TESTING', 3],
          ['TESTING', 1],
          ['TESTING', 1],
          ['TESTING', 1],
          ['TESTING', 2]
        ]);

        // Set chart options
        var options = {'title':'How Much Pizza I Ate Last Night',
                       'width':400,
                       'height':300};

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('testingHidden'));
		
        chart.draw(data, options);
      }
	  
	 function loadChart2(element) {
		google.load("visualization", "1", {packages:["corechart"]});
		 
		 
		google.setOnLoadCallback(function() {
			$(function() {
				//alert("draw chart 2.2");
				// init my stuff
				var data = new google.visualization.DataTable();
			  
				// Added data

				var chart = new google.visualization.LineChart(document.getElementById(element));
				chart.draw(data, {width: 900, height: 400});
			});
		});
		 
		 google.setOnLoadCallback( function() {
		 
			  
			  
		 });
	 }
	 
	 function testCall(){
		//alert("in TestCall");
		document.write("inside gchart!");
	}
