    	



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

    function drawKeywordBarGraph(graphLoc) {

	$.ajax({
            url: "./keyentjsonservlet",
            success: function (array) {
                var obj = jQuery.parseJSON(array);
                //console.log(obj.keyEnt);
				var array_data = new Array();
				var i = 0;
				$.each(obj.keyEnt, function(key,value){
					var keyword,score;
					$.each(value, function(k,v){
						if(k == 'keyword'){
							keyword = v;
						}
						else if(k == 'score'){
							score = v;
						}
					});
					array_data[i] = new Array();
					array_data[i][0] = keyword;
					array_data[i][1] = score;
					i++
				});
				console.log(array_data);
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'keyword');
                data.addColumn('number', 'score');
                data.addRows(array_data);

                var options = {
                    'title': 'Trending topics',
					'width': 400,
					'height': 300
                };

                var chart = new google.visualization.BarChart(document.getElementById('graphLeft'));
                chart.draw(data, options);
            }
        });      
      }
/****************************************************************/


    function userTopKarma(jsonData) {

    	
           	var obj = jsonData;
    		var array_data = new Array();
    		var i = 0;
    		userData = obj.user;
    		$.each(userData, function(key,value){
    	
    			if(value.karma!=undefined)
    			{
    				array_data[i] = new Array();
    				array_data[i][0] = key;
    				array_data[i][1] = value.karma.value;
    				i++;
    			}
    			
    		});
    		console.log(array_data);
                	var data = new google.visualization.DataTable();
                	data.addColumn('string', 'User');
                	data.addColumn('number', 'score');
                	data.addRows(array_data);

                	var options = {
                	    'title': 'userTopKarma',
    			'width': 400,
    			'height': 300
                	};

    	               var chart = new google.visualization.BarChart(document.getElementById('graphTopLeft'));
         	       chart.draw(data, options);
    }


    function userTopNumCom(jsonData) {

           	var obj = jsonData;
    		var array_data = new Array();
    		var i = 0;
    		userData = obj.user;
    		$.each(userData, function(key,value){
    			if(value.comments!=undefined)
    			{
    				array_data[i] = new Array();
    				array_data[i][0] = key;
    				array_data[i][1] = value.comments.value;
    				i++;
    			}
    			
    		});
    			console.log(array_data);
                    	var data = new google.visualization.DataTable();
                    	data.addColumn('string', 'User');
                    	data.addColumn('number', 'score');
                    	data.addRows(array_data);

                    	var options = {
                    	    'title': 'userTopNumCom',
    				'width': 400,
    				'height': 300
                    	};

     	               var chart = new google.visualization.BarChart(document.getElementById('graphTopRight'));
             	       chart.draw(data, options);
    }

    function userTopNumLinks(jsonData) {

            var obj = jsonData;
    		var array_data = new Array();
    		var i = 0;
    		userData = obj.user;
    		$.each(userData, function(key,value){
    			if(value.contribution!=undefined)
    			{
    				array_data[i] = new Array();
    				array_data[i][0] = key;
    				array_data[i][1] = value.contribution.value;
    				i++;
    			}
    			
    		});
    			console.log(array_data);
                    	var data = new google.visualization.DataTable();
                    	data.addColumn('string', 'User');
                    	data.addColumn('number', 'score');
                    	data.addRows(array_data);

                    	var options = {
                    	    'title': 'userTopNumLinks',
    				'width': 400,
    				'height': 300
                    	};

     	               var chart = new google.visualization.BarChart(document.getElementById('graphBotLeft'));
             	       chart.draw(data, options);
    }

    function userTotNumVisits(jsonData) {

            var obj = jsonData;
    		var array_data = new Array();
    		var i = 0;
    		userData = obj.user;
    		$.each(userData, function(key,value){
    			if(value.visits!=undefined)
    			{
    				array_data[i] = new Array();
    				array_data[i][0] = key;
    				array_data[i][1] = value.visits.value;
    				i++;
    			}
    			
    		});
    			console.log(array_data);
                    	var data = new google.visualization.DataTable();
                    	data.addColumn('string', 'User');
                    	data.addColumn('number', 'score');
                    	data.addRows(array_data);

                    	var options = {
                    	    'title': 'userTotNumVisits',
    				'width': 400,
    				'height': 300
                    	};

     	               var chart = new google.visualization.BarChart(document.getElementById('graphBotRight'));
             	       chart.draw(data, options);
    }
    
    google.load("visualization", "1", {packages:["table"]});
    
    function drawTable(jsonData) {
              	 var data = new google.visualization.DataTable();
              	 data.addColumn('string', 'username');
  		          data.addColumn('number', 'Total Karma');
  		          data.addColumn('number', 'Total Comments');
  		          data.addColumn('number', 'Total Contribution');
  		          data.addColumn('number', 'Total Visits');
  		          
  		          
              	 
              	 var Rows = [];
              	 var tableRow;
                   var obj = jsonData;
                   console.log(obj.user);
                    $.each(obj.user,function(key,value){
                  	console.log(key + ":" + value);
                  	tableRow = new Array();
                  	var userProfile = "<a href = 'http://www.reddit.com/user/"+key+ "'>"+key+"</a>";
                  	
                  	tableRow.push(userProfile);
                  	if(value.karma!=undefined)
                  	{
                  		console.log(value.karma+":"+value.karma.rank + "," + value.karma.value+ "|");
                  		tableRow.push(value.karma.rank);
                  	}
                  	
                  	else{
                  		tableRow.push(null);
                  	}
                  	if(value.comments!=undefined)
                  	{
                  		console.log(value.comments+":"+value.comments.rank + "," + value.comments.value+ ":" );
                  		tableRow.push(value.comments.rank);
                  	}
                  	else
                  	{
                  		tableRow.push(null);
                  	}
                  	
                  	if(value.contribution!=undefined)
                  	{
                  		console.log(value.contribution+":"+value.contribution.rank + "," + value.contribution.value+ ":");
                  		tableRow.push(value.contribution.rank);
                  	}
                  	else{
                  		tableRow.push(null);
                  	}
                  	
                  	if(value.visits!=undefined)
                  	{
                  		console.log(value.visits+":"+value.visits.rank + "," + value.visits.value+ ":");
                  		tableRow.push(value.visits.rank);
                  	}
                  	else{
                  		tableRow.push(null);
                  	}
                  	Rows.push(tableRow);
                   }); 
                  	 
                   
                   console.log(Rows);
  		           data.addRows(Rows);
  		          
  		           var chart = new google.visualization.Table(document.getElementById('userTable'));
  		           var cellFormatter = new google.visualization.ColorFormat();
  		           cellFormatter.addGradientRange(1, 11,'white' ,'#ff0048', '#00baff');
  		           cellFormatter.format(data,1);
  		           cellFormatter.format(data,2);
  		           cellFormatter.format(data,3);
  		           cellFormatter.format(data,4);
  		           
  		          chart.draw(data, {showRowNumber: false, allowHtml: true,height:'800'});  
     	  
     	 show('userTable');

    	}
