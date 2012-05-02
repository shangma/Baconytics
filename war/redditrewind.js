    google.load("jquery", "1");
    google.load("jqueryui", "1");
    
    var playing = false;
    var ppString = ["Play","Pause"];
    var step = 2000;
    
    function faster() { step *= 0.5; if (step < 10)  step = 10; }
    function slower() { step *= 2;   if (step > 10000) step = 10000;}
	function reset() {
		if (playing) playpause();
		statCount = 0;
		now = oldnow = source.linkStats[0].last_seen;
		step = 2000;
	}
    function playpause() { 
		$("#playstring").html((playing = !playing) ? ppString[1] : ppString [0]); 
		if (playing) loop();
	}
	
	var dirstr = {"-1": "up", "1": "down", "0": "same"};
    
	var source = null;
	var statCount = 0;
	var era = 5*60*1000;                                 
	var now = null;
	var oldnow = null;
	var max = 10;
	var maxinmem = 200;
	var oldkarma = null;
	
	var mutex = false;
    function loop() {
		if (!playing || mutex) { setTimeout(loop, step); return; }
		mutex = true;
		
		
		var done = true;
		if (++statCount < source.linkStats.length) { 
		
		var group = source.linkStats[statCount];
		
		now = group.time_seen;
		console.log(now + " - " + new Date(oldnow).toString("dddd MMM d yyyy hh:mm tt"));
				
		var major = false;
		if (now - oldnow > era) {
			$("#timestring").html(new Date(oldnow).toString("dddd MMM d yyyy hh:mm tt"));
			updateRanks();
			oldnow = now;
			major = true;
		}
		} else {
		console.log("done"); playpause();
		reset();
		}
		
		$.each(group.stats, function(key, val) { updateLinkStats(val); });
		
		mutex = false;
		setTimeout(loop, major ? step : 0);
	}
	var active = {};
	function updateRanks() {
		//sorting v3	
		
		for (var k in active) {
			if (active[k].gen++ > 2) {
				$(active[k].pointer).animate({
				"top": 10000+"px",
				"opacity": 0.0
			},{
				"duration": step/2,
				"queue": q,
				"complete": function() {
					$(this).hide().remove();
				}
			});
			delete active[k];
			}
		}
		
		//updateLinkStats adds link to active on every update
		
		//sort active links by karma
		var q = false; //"aq" + now;
		var keys = [];
		for (var k in active) keys.push(k);
		keys.sort(byScore);
		
		console.log("min: "+keys[keys.length-1]+"("+active[keys[keys.length-1]].score+","+(keys.length-1)+"), max: "+keys[0]+"("+active[keys[0]].score+")");
		
		//calculate new offsets for < max
		//animate to new offsets and ranks in a batch, set up/down votes
		var i, offset = 0;
		var karmatotal = 0;
		for (i = 0; i < max && i < keys.length; i++) {
			karmatotal += active[keys[i]].score;
			var dir = 0;
			if ("rank" in active[keys[i]]) {
				if (active[keys[i]].rank > i) {
					dir = -1;
				}
				if (active[keys[i]].rank < i) {
					dir = 1;
				}
			} else {
				dir = -1;
			}
			active[keys[i]].rank = i;
			
			var a = active[keys[i]].pointer;
			
			if (dir == -1) {
				$(a).find(".midcol > .uparrow").addClass("upmod").removeClass("up");
			} else if (dir == 1) {
				$(a).find(".midcol > .downarrow").addClass("downmod").removeClass("down");
			}
			
			$(a).show().animate({
				"top": offset+"px",
				"opacity": 1.0
			},{
				"duration": step/2,
				"queue": q,
				"complete": function () {
					$(this).find(".upmod").removeClass("upmod").addClass("up");
					$(this).find(".downmod").removeClass("downmod").addClass("down");
				}
			});
			
			var span = new TimeSpan(now-active[keys[i]].time_seen);
			var age = 0; var unit = null;
			if (age == 0) { age = span.getDays();    unit = "days"; }
			if (age == 0) { age = span.getHours();   unit = "hours"; }
			if (age == 0) { age = span.getMinutes(); unit = "minutes"; }
			if (age == 0) { age = span.getSeconds(); unit = "seconds"; }
			$(a).find(".age").html(age + " " + unit + " ago");
			
			$(a).find(".rank").html(i+1);
			console.log(keys[i] + " is " + dirstr[dir] + " to " +i+" with "+(active[keys[i]].score)+" at "+ offset+"px");
			offset += active[keys[i]].height;
		}
		
		if (oldkarma == null) {
			oldkarma = totalkarma;
		} else {
			var d = totalkarma - oldkarma;
			$("#karmicdelta").html(d+"&nbsp;&nbsp;"+(d>0?"up":"down")+"votes");
		}
		
		//fade out prune > max from dom and active
		while (i < keys.length && i < maxinmem) {
			$(active[keys[i]].pointer).animate({
				"top": offset+"px",
				"opacity": 0.0
			},{
				"duration": step/2,
				"queue": q,
				"complete": function() {
					$(this).hide();
				}
			});
			i++;
		}	
		
		while (i < keys.length) {
			//delete active[keys[i++]];
		}
		
		$("#siteTable").css({"height": offset+"px"});
		
		return;
		console.log(q + ": "+$.queue(q).length);
		while ($.queue(q).length > 0) $.queue(q).dequeue();
	}
	
	function byScore(a,b) {
		a = active[a];
		b = active[b];
		if (a.score < b.score)
			return 1;
		if (a.score > b.score)
			return -1;
		return 0;
	}
	
	if (true) {
	
	if (navigator.userAgent.indexOf("Firefox") != -1) {
    try {
        netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");
    } 
    catch (e) {
        alert("Permission UniversalBrowserRead denied -- not running Mozilla?");
    }
}
}
	
	function onLoad() {
		setTimeout(function() { 
			$.getJSON("api?action=getLinkStats", function(data) {
				source = data;
				console.log(source);
				now = oldnow = source.linkStats[0].time_seen;
				$(".tabmenu li a").show();
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
		$(c).css({"top":"1000px"});
		$(c).appendTo("#siteTable");
		return c;
	}
	
	function updateLinkStats(linkStat) {
		//console.log(linkStat.id + " updated "+(now-linkStat.time_seen) + " seconds ago");
		var c = $("#"+linkStat.id);
		if ($(c).length == 0) c = newLinkDiv(linkStat.id);
		var s = $(c).find(".entry > .tagline");
		$(c).find(".midcol > .score").html(linkStat.score);
		$(c).find(".entry > ul > li.first > a.comments").html(linkStat.num_comments+" comments");	
		$(s).find("span > .res_post_ups").html(linkStat.ups);
		$(s).find("span > .res_post_downs").html(linkStat.downs);
		
		if (!(linkStat.id in active)) {
			active[linkStat.id] = { "pointer": c, "height": $(c).height(), "time_seen": now };
		}
		active[linkStat.id].score = linkStat.score;
		active[linkStat.id].gen = 0;
		//active[linkStat.id].time_seen = now;

	}
	
    google.setOnLoadCallback(onLoad);