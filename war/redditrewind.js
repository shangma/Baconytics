    google.load("jquery", "1");
    google.load("jqueryui", "1");
	goog.require('goog.dom')
    
    var playing = false;
    var ppString = ["Play","Pause"];
    var step = 500;
    
    function slower() { step *= 0.5; if (step < 100)  step = 100; }
    function faster() { step *= 2;   if (step > 2000) step = 2000;}
    function playpause() { 
		$("#playstring").html((playing = !playing) ? ppString[1] : ppString [0]); 
		if (playing) loop();
	}
    
	var source = null;
	var statCount = 0;
	var era = 10*60*1000;                                 
	var now = null;
	
	var debug = 20;
	
    function loop() {
		if (!playing) { setTimeout(loop, step); return;    }
		if (now == null) {
			now = new Date(source.linkStats[0].time_seen);
		} else {
			now = new Date(now.getTime()+era);
		}
		$("#timestring").html(now.toString("dddd MMM d yyyy hh:mm tt"));
		
		var done = true;
		while (done) {
			var group = source.linkStats[statCount++];
			if (group.time_seen > now) done = false;
			$.each(group.stats, function(key, val) {
				if (debug-- == 0) return;
				updateLinkStats(val);
			});
			//TODO: updateRanks();
		}
		playpause();
		setTimeout(loop, step);
	}
	var active = [];
	function updateRanks() {
		//sorting v3
		
		//updateLinkStats adds link to active on every update
		//sort active links by karma
		//calculate new offsets for < max
		//fade out prune > max from dom and active
		//animate to new offsets and ranks in a batch, set up/down votes
		//setTimeout to remove up/down votes (fade?)
	}
	
	/*
	css for upvotes
	
	$(a).find(".midcol > .uparrow").addClass("upmod").removeClass("up");
	$(b).find(".midcol > .downarrow").addClass("downmod").removeClass("down");
	
	$(".uparrow").addClass("up").removeClass("upmod");
	$(".downarrow").addClass("down").removeClass("downmod");
	*/
	
    function onLoad() {
		setTimeout(function() { 
			$.getJSON("api.json", function(data) {
				source = data;
				console.log(source);
				$("#timestring").html("Ready");
			});
		}, 200);
	}
	function newLinkDiv(linkid) {
		var link = source.link[linkid];
		var c = $("#protoman").clone(true).addClass("real");
		$(c).attr('id', linkid);
		$(c).find(".entry > .title >.domain").html(link.domain);
		$(c).find(".entry > .title > a.title").html(link.title);
		$(c).find(".entry > .tagline > .author").html(link.author);
		$(c).find(".entry > .tagline > .subreddit").html(link.subreddit);
		$(c).appendTo("#siteTable");
		return c;
	}
	
	var toplinks = [];
	
	function updateLinkStats(linkStat) {
		var c = $("#"+linkStat.id);
		if ($(c).length == 0) c = newLinkDiv(linkStat.id);
		var s = $(c).find(".entry > .tagline");
		$(c).find(".midcol > .score").html(linkStat.score);
		$(c).find(".entry > ul > li.first > a.comments").html(linkStat.num_comments+" comments");	
		$(s).find("span > .res_post_ups").html(linkStat.ups);
		$(s).find("span > .res_post_downs").html(linkStat.downs);
		
		var span = new TimeSpan(new Date(linkStat.time_seen)-now);
		var age = 0; var unit = null;
		if (age == 0) { age = span.getDays();    unit = "days"; }
		if (age == 0) { age = span.getHours();   unit = "hours"; }
		if (age == 0) { age = span.getMinutes(); unit = "minutes"; }
		if (age == 0) { age = span.getSeconds(); unit = "seconds"; }
		
		$(s).find(".age").html(age + " " + unit + " ago");
		/*
		//sorting v2
		
		var found = false;
		var offset = 0;
		var max = 100;
		var i;
		var myheight = $(c).height() + 5;
		if (toplinks.length == 0) {
			console.log("1");
			toplinks[0] = {"id": linkStat.id, "score": linkStat.score, "height": myheight, "elem": c};
			$(c).show().animate({"top":"0px", "opacity": 1.0}, 1000);
		} else for (i = 0; i < toplinks.length+1; i++) {
			if (i == toplinks.length) {
				if (!found) {
				console.log("5 "+i+" "+offset);
				toplinks[toplinks.length]={"id": linkStat.id, "score": linkStat.score, "height": myheight, "elem": c};
				$(c).show().animate({"top":offset+"px", "opacity": 1.0}, 1000);
				$(c).find(".rank").html(i+1);
				offset += myheight;
				found = true;
				}
			} else if (linkStat.score > toplinks[i].score) {
				if (!found) {
				console.log("9 "+i+" "+offset);
				toplinks.splice(i,0,{"id": linkStat.id, "score": linkStat.score, "height": myheight, "elem": c});
				$(c).animate({"top":offset+"px"}, 1000);
				$(c).find(".rank").html(i+1);
				offset += myheight;
				found = true;
				}
			} else if (i > max) {
				console.log("6 "+i);
				$(c).animate({"opacity": 0.0}, 500, "linear", function() { $(this).remove(); });
				delete toplinks[i--];
			} else if (linkStat.id == toplinks[i].id) {
				console.log("4 "+i);
				//toplinks.splice(i--,1);
			} else if (found) {
				console.log("7 "+i+" "+offset);
				$(toplinks[i].elem).animate({"top":offset+"px"}, 1000);
				$(c).find(".rank").html(i+1);
				offset += toplinks[i].height;
			} else if (linkStat.score < toplinks[i].score) {
				console.log("3 "+i+" "+offset);
				offset += toplinks[i].height;				
			}
		}
		console.log("e");
	
		//====
		//sorting v1
		var prev = $(c);
		while((prev = $(c).prev(".real")) && $(prev).size() > 0 && source.link[$(prev).attr('id')].score && source.link[$(prev).attr('id')].score < linkStat.score);
		if (prev && $(prev).attr('id') != linkStat.id) $(c).after($(prev));
		var next = $(c);
		while((next = $(c).next(".real")) && $(next).size() > 0 && source.link[$(next).attr('id')].score && source.link[$(next).attr('id')].score > linkStat.score);
		if (next && $(next).attr('id') != linkStat.id) $(c).before($(next));
		
		while($(c).siblings(".real").size() > 25) $(c).siblings(".real:last-child").remove();
		*/
	}
        
    function createPostDiv(id, posttime, subreddit, author, title, domain, karma, ups, downs, numcomments) {
	age = "42 hours ago";
	
    var c = $("#protoman").clone(true).attr("id", id).addClass("real");
	$(c).find(".entry > .title >.domain").html(domain);
	$(c).find(".entry > .title > a.title").html(title);
	$(c).find(".entry > .tagline > .author").html(author);
	$(c).find(".entry > .tagline > .subreddit").html(subreddit);
	$(c).appendTo("#siteTable").show();
	
	
	updateDiv(id, karma, numcomments, ups, downs, age);
	
	$(c).show();
    }
    function updateDiv(id, karma, numcomments, ups, downs, age) {
	var c = $("#"+id);
	var s = $(c).find(".entry > .tagline");
	
	$(c).find(".midcol > .score").html(karma);
	$(c).find(".entry > ul > li.first > a.comments").html(numcomments+" comments");	
	$(s).find("span > .res_post_ups").html(ups);
	$(s).find("span > .res_post_downs").html(downs);
	$(s).find(".age").html(age);
    }
    google.setOnLoadCallback(onLoad);