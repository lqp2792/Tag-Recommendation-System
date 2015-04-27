/**
 * Phú Lê
 */
var title = null;
var url = null;
var offset = 0;
var scene;
var searchTag;
var defaultSubscriptionTags = [];
var subscriptionTags = [];
var totalDiscoverTags = [];
var taggedTags = [];
var deletedTags = [];
/* =============================================== */
/* === Thay đổi page content, page header == */
/* =============================================== */
$(document).ready(function() {
	if (document.URL.indexOf('dashboard') > -1) {
		accessDashboardPage();
	}
	if (document.URL.indexOf('network') > -1) {
		accessNetworkPage();
	}
	if(document.URL.indexOf('discover') > -1) {
		accessDiscoverPage();
	}
	if(document.URL.indexOf('trending') > -1) {
		accessTrendingPage();
	}
});

function accessDashboardPage() {
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/dashboard/getBookmarks',
		data : {
			'offset' : offset
		}, success : function(data) {
			console.log('Access Dashboard : ' + data);
			var content = $('.dashboard').eq(1);
			if(data.status == "SUCCESS") {
				$(content).html('');
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				inner += 'You did not post any bookmarks. Please post your most interested bookmarks!';
				inner += '</p></div></div>';
				$(content).html(inner);
			}	
		}
	}) ;
}

function accessNetworkPage() {
	// Khi click vào menu Network, thì sẽ mở trang mơi -> link bị thay đổi -> hàm ajax này sẽ chạy đầu tiên
	$('.dashboard').removeClass('show').addClass('hidden');
	$('.network').removeClass('hidden').addClass('show');
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/network/getBookmarks',
		data : {
			'sortBy' : sortBy,
			'offset' : offset
		},
		success : function(data) {
			console.log(data);
			var content = $('.network').eq(1);
			$(content).html('<div class="row sort-by" style="padding-bottom: 18px; border-bottom: 1px solid #EEE;">' 
					+ '<div class="col-lg-2"><h4 class="text-info"><i class="fa fa-sort"></i> Sort By:</h4></div><div class="col-lg-2"><button id="ar" class="btn btn-primary btn-block">Average Rating</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="vt" class="btn btn-primary btn-block">View Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="ct" class="btn btn-primary btn-block">Copy Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="pt" class="btn btn-primary btn-block">Posted Time</button</div></div>');
			switch(sortBy) {
			case 1: $('#ar').html('<i class="fa fa-check-circle"></i> Average Rating'); break;
			case 2: $('#vt').html('<i class="fa fa-check-circle"></i> View Times'); break;
				case 3: $('#ct').html('<i class="fa fa-check-circle"></i> Copy Times'); break;
			case 4: $('#pt').html('<i class="fa fa-check-circle"></i> Posted Time'); break;
			}
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				if(sortBy == 1) {
					inner += 'There are no bookmarks has average rating > 0 from friends';
				}
				if(sortBy == 2) {
					inner += 'There are no bookmarks has view times > 0 from friend';
				}
				if(sortBy == 3) {
					inner += 'There are no bookmarks has copy times > 0 from friends';
				}
				if(sortBy == 4) {
					inner += 'There are no bookmarks has posted from friends';
				}
				inner += '</p></div></div>';
				$(content).append(inner);
			}
		}
	});
}

function accessDiscoverPage() {
	$('.dashboard').removeClass('show').addClass('hidden');	
	$('.discover').removeClass('hidden').addClass('show');
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/discover/subscription',
		success : function(data) {
			defaultSubscriptionTags = data.result[0];
			subscriptionTags = data.result[1];
			totalDiscoverTags = [];
			var text = 'Your most used tags: ';
			for (i = 0; i < defaultSubscriptionTags.length; i++) {
				text += '<span>(' + defaultSubscriptionTags[i].weight + ')<code>#' + defaultSubscriptionTags[i].tag + '</code></span>';
			}
			$('#default-subscription-tags').html(text);
			var subscription = null;
			if (subscriptionTags.length == 0) {
				subscription = '<h4 class="text-warning">You did not set any subcription tags!</h4>';
				$('#subscription-tags').html(subscription);
				$('.discover').eq(1).html('<p class="text-center text-info">System will use'
										+ ' top most used tags for subscription as default!</p>');
				for(i=0; i<defaultSubscriptionTags.length; i++) {
					totalDiscoverTags.push(defaultSubscriptionTags[i].tag);
				}
			} else {
				var inner = 'Your subscription tags:  ';
				for (i = 0; i < subscriptionTags.length; i++) {
					inner += '<span><code>#' + subscriptionTags[i] + '</code></span>';
					totalDiscoverTags.push(subscriptionTags[i]);
				}
				$('#subscription-tags').html(inner);
			}
			discoverBookmarks(totalDiscoverTags);
		}
	});
}

function accessTrendingPage() {
	$('.dashboard').removeClass('show').addClass('hidden');	
	$('.trending').removeClass('hidden').addClass('show');
	if(!$('#myCanvas').tagcanvas({
		textColour : '#0066CC',
		outlineColour : '#ff9999',
		outlineThickness : 1,
		maxSpeed : 0.03,
		textHeight: 20,
		depth : 0.99,
		weight : true
	}, 'tagList')) {
		$('#myCanvasContainer').hide();
	}
	
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/trending/bookmarks',
		success : function(data) {
			$('.trending').eq(1).html('');
			for(i=0; i<15; i=i+3) {
				var inner = '<div class="row" style="border-bottom: 2px solid #EEE; padding-bottom: 10px">';
				inner += '<div class="col-lg-4"><section class="trending-bookmark"><h4>' + data.result[i].url + '</h4></section><div class="urlive-container"></div></div>';
				inner += '<div class="col-lg-4"><section class="trending-bookmark"><h4>' + data.result[i + 1].url + '</h4></section><div class="urlive-container"></div></div>';
				inner += '<div class="col-lg-4"><section class="trending-bookmark"><h4>' + data.result[i + 2].url + '</h4></section><div class="urlive-container"></div></div>';
				inner += '</div>';
				$('.trending').eq(1).append(inner);
			}
			$('.trending-bookmark').each(function(index) {   
			    $(this).urlive({
			    	imageSize : 'small',
			    	container: '.urlive-container:eq('+ index + ')'
		    	});   
			});
		}
	});
}
/* =============================================== */
$(document).ready(function() {
	$('#add-bookmark-modal').on('hidden.bs.modal', function(e) {
		$(this).find('#new-bookmark-textarea').val('');
		var submitButton = $('#add-bookmark-submit');
		if (submitButton.hasClass('fa-spin')) {
			submitButton.removeClass('fa-spinner').removeClass('fa-spin').addClass('fa-plus');
		}
	});
	$('#add-bookmark-modal').on('shown.bs.modal', function(e) {
		var submitButton = $('#add-bookmark-submit');
		if (submitButton.hasClass('fa-spin')) {
			submitButton.removeClass('fa-spinner').removeClass('fa-spin').addClass('fa-plus');
		}
	});
	$('#add-tag-modal').on('shown.bs.modal', function() {
		$('#bookmark-tags').val('');
		getRecommendTag();
		var submitButton = $('#add-tag-submit');
		if (submitButton.hasClass('fa-spin')) {
			submitButton.removeClass('fa-spinner').removeClass('fa-spin').addClass('fa-plus');
		}
	});
	$('#add-tag-form').submit(function(e) {
		e.preventDefault();
		submitBookmark();
	});
	$('[data-toggle="tooltip"]').tooltip();
	$('#log-out').click(function() {
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/logout',
			success : function(data) {
				window.location.href = '/TagRecommend';
			}
		});
	});
	/* Click my bookmarks menu javascript */
	$('#my-bookmarks-menu').click(function() {
		clickMyBookmarks();
	});
	$('#nw-add-tag-modal').on('shown.bs.modal', function() {
		$('#nw-tag').val('');
	});
	$('#nw-add-tag-modal').on('hidden.bs.modal', function() {
		$('#nw-tag').val('');
	});
});

/* =============================================== */
/* === Lấy các tag được gợi ý khi post bookmark == */
/* =============================================== */
function getRecommendTag() {
	$('.waiting-div').html('<div id="waiting-message-tag"><i class="fa fa-spinner fa-spin"></i> Getting Recommend Tag</div>');
	$.ajax({
		type : "POST",
		url : "/TagRecommend/dashboard/gettags",
		data : {
			"title" : title,
			"url" : url,
		},
		success : function(data) {
			console.log(data);
			recommended = true;
			for (i = 0; i < data.result.length; i++) {
				$('#recommend-tags-div').append(
						'<strong>Topic ' + data.result[i].topicID + " - "
								+ data.result[i].topicProbality.toFixed(2)
								+ "</strong><br /><div>");
				var tags = data.result[i].recommendTags;
				for (j = 0; j < tags.length; j++) {
					$('#recommend-tags-div').append(
							'<code class="recommend-tag" style="cursor : copy"> #'
									+ tags[j].content + ' </code>');
				}
				$('#recommend-tags-div').append('</div><br />');
			}
			$('.recommend-tag').click(
					function() {
						var content = $('#bookmark-tags').val();
						if (content == null) {
							$('#bookmark-tags').val($(this).text().trim());
						} else {
							$('#bookmark-tags').val(content + ' ' + $(this).text().trim());
						}
					});
		},
		complete : function() {
			$('#waiting-message-tag').remove();
		}
	});
};
/* =============================================== */
/* ===== Submit bookmark sau khi đã add tag ====== */
/* =============================================== */
function submitBookmark() {
	$("#add-tag-submit").removeClass('fa-plus').addClass('fa-spinner').addClass('fa-spin');
	$.ajax({
		type : "POST",
		url : "/TagRecommend/dashboard/addBookmark",
		data : {
			"title" : title,
			"url" : url,
			"tags" : $('#bookmark-tags').val(),
			"comment" : $('#bookmark-comment').val()
		},
		success : function(data) {
			$('#add-tag-modal').modal('hide');
			console.log(data);
			window.location.href = '/TagRecommend/dashboard';
		}
	});
};
/* =============================================== */
/* Click My Bookmark quay về trang đầu */
/* =============================================== */
function clickMyBookmarks() {
	window.location.href = '/TagRecommend/dashboard';
}
/* =============================================== */
/* Click follow người dùng khác */
/* =============================================== */
$(document).ready(function() {
	$('.follow').on("click", function(event) {
		$(this).html('<i class="fa fa-check-circle"></i> Followed');
		$('#network-waiting').removeClass('hidden').addClass('show');
		var col = $(this).closest('col-md-15');
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/network/follow',
			data : {
				targetUserID : $('.userID').attr('value')
			},
			success : function(data) {
				window.location.reload(true);
			}
		});
	});
});
/* =============================================== */
/* Scroll Load thêm content */
/* =============================================== */
function infiniteScroll(content) {
	var controller = new ScrollMagic.Controller();
	scene = new ScrollMagic.Scene({
		triggerElement : "#loader", triggerHook : "onEnter"
	}).addTo(controller).on("enter", function(e) {
		if (!$("#loader").hasClass("active")) {
			$("#loader").addClass("active");
			if (console) {
				console.log("Loading New Items");
			}
			setTimeout(addElements, 1000, content);
		}
	});

	function addElements(content) {
		console.log('Get New Elements');
		if($(content).hasClass('search')) {
			var ajaxUrl = '/TagRecommend/search';
			var ajaxData = {'sortBy' : sortBy, 'offset' : offset, 'tag' : searchTag};
		}
		if($(content).hasClass('network')) {
			var ajaxUrl = '/TagRecommend/network/getBookmarks';
			var ajaxData = {'sortBy' : sortBy, 'offset' : offset};
		}
		if($(content).hasClass('dashboard')) {
			var ajaxUrl = '/TagRecommend/dashboard/getBookmarks';
			var ajaxData = {'offset' : offset};
		}
		if($(content).hasClass('discover')) {
			var ajaxUrl = '/TagRecommend/discover/discoverBookmarks';
			var ajaxData = {'subscriptionTags' : totalDiscoverTags, 'sortBy' : sortBy, 'offset' : offset};
		}
		$.ajax({
			type : 'GET',
			url : ajaxUrl,
			data : ajaxData,
			traditional : true,
			success : function(data) {
				loadBookmarks(content, data);
				$('#loader').remove();
				if (data.result.length == 5) {
					$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
					offset += 5;
				} else {
					scene.remove();
					$(content).append('<div class="center-div"><h4><a href="#" id="backToTop"><i class="fa fa-reply"></i> THERE IS NO MORE BOOKMARKS</a></h4></div>');
				}
			}
		});
		scene.update();
	}
}
/* =============================================== */
/* Click link quay trở về trang đầu */
/* =============================================== */
$(document).ready(function($) {
	$(window).scroll(function() {
		if ($(this).scrollTop() > 50) {
			$('#backToTop').fadeIn('slow');
		} else {
			$('#backToTop').fadeOut('slow');
		}
	});
	$('#backToTop').click(function() {
		$("html, body").animate({
			scrollTop : 0
		}, 500);
		return false;
	});
});

/* =============================================== */
/* Người paste link và click gửi lên server */
/* =============================================== */
$(document).ready(function() {
	$('#add-bookmark-form').submit(function(e) {
		e.preventDefault();
		$("#add-bookmark-submit").removeClass('fa-plus').addClass('fa-spinner').addClass('fa-spin');
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/dashboard/checkBookmark',
			data : {
				'url' : $('#new-bookmark-textarea').val()
			},
			success : function(data) {
				console.log(data);
				if (data.status == 'SUCCESS') {
					title = data.result.title;
					url = data.result.url;
					$('#bookmark-url').html(data.result.url);
					$('#bookmark-title').html(data.result.title);
					$('#add-bookmark-modal').modal('hide');
					$('#add-tag-modal').modal('show');
				} else {
					var submitButton = $('#add-bookmark-submit');
					if (submitButton.hasClass('fa-spin')) {
						submitButton.removeClass('fa-spinner').removeClass('fa-spin').addClass('fa-plus');
					}
					var errorHtml = '<div id="error-message"><br/><i class="fa fa-times"> </i>'
							+ '<span class="text-danger"> ' + data.result + ' </span></div>';
					$("#new-bookmark-textarea").after(errorHtml);
					var timeout = setTimeout(function() {
						$('#error-message').remove();
					}, 3000);
				}
			}
		});
	});
});
/* =============================================== */
/* Xử lí dữ liệu rating */
/* =============================================== */
function createRatingChoices(id, rating) {
	var id = '#' + id;
	$(id).rating({
		starCaptions: {'1': "Very Poor", '1.5' : 'Quite Poor', '2': "Poor", '2.5' : 'Quite Fair', '3': "Fair", '3.5' : 'Acceptable', '4': "Quite Good", '4.5' : 'Good', '5': "Very Good"},
	    starCaptionClasses: {'1': "text-danger", '1.5' : "text-danger", '2': "text-warning", '2.5' : "text-warning", '3': "text-info", '3.5' : "text-primary", 4: "text-primary", '4.5' : "text-success", 5: "text-success"},
	});
	$(id).rating('update', rating); 
	$(id).on('rating.change', function(e, value, caption) {
		var bookmark = $(id).closest('.bookmark');
		var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
		console.log('Rating bookmark ID ' + bookmarkID + ' - Rating: ' + value);
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/network/rateBookmark',
			data : {
				'rating' : value,
				'bookmarkID' : bookmarkID
			}, success : function(data) {
				console.log('Total Rating: ' + data.result);
				$(bookmark).find('.avg-rating').text('Avg Rate: ' + data.result.toFixed(2));
			}
		});
	});
}
/* =============================================== */
/* Hiện thông tin về rating, tags khi click */
/* =============================================== */
$(document).on("click", ".view-tags", function(e) {
	var viewTag = $(this).closest('.bookmark').find('.tags-jumbotron');
	if($(viewTag).hasClass('show')) {
		$(viewTag).removeClass('show').addClass('hidden');
	} else {
		$(viewTag).removeClass('hidden').addClass('show');
		var bookmark = $(this).closest('.bookmark');
		var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
		console.log('View other tags - bookmark : ' + bookmarkID);
		var postedUserID = bookmark.find('input[name="postedUserID"]').attr('value');
		if($('.dashboard').hasClass('show')) {
			var ajaxUrl = '/TagRecommend/dashboard/getOtherTags';
			var ajaxData = { 'bookmarkID' : bookmarkID};
		} else {
			var ajaxUrl = '/TagRecommend/network/getOtherTags';
			var ajaxData = { 'bookmarkID' : bookmarkID, 'postedUserID' : postedUserID};
		}
		$.ajax({
			type : 'GET',
			url : ajaxUrl,
			data : ajaxData,
			success : function (data) {
				console.log(data);
				if(data.status == "SUCCESS") {
					for(i=0; i<data.result.length; i++) {
						$(viewTag).html('');
						var additionalTag = data.result[i];
						var inner = '<div class="row"><div class="col-lg-2">';
						inner += additionalTag.firstName + ' ' + additionalTag.lastName + ': </div>';
						inner += '<div class="col-lg-10"><div class="inline-div">';
						for (j = 0; j < additionalTag.tags.length; j++) {
							var tag = additionalTag.tags[j];
							var id = 'menu' + '-' + additionalTag.userID + '-' + bookmarkID + '-' + j;
							inner += '<div class="dropdown inline-div">';
							inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
									+ id + '">#' + tag + '</code>';
							inner += '<ul class="dropdown-menu" role="menu" aria-labelledby="' + id + '">';
							inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Filter by #' + tag + '</a></li>';
							inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"> Find #' + tag + ' on System</a></li>';
							inner += '</ul></div>';
						}
						inner += '</div></div></div>';
						$(viewTag).append(inner);
					}
				} else {
					$(viewTag).html('<div class="text-center"><small class="text-warning">No additional tags</small></div>');
				}
			}
		});
	}
});
$(document).on("click", ".view-rating", function(e) {
	var viewRating = $(this).closest('.bookmark').find('.rating-jumbotron');
	if($(viewRating).hasClass('show')) {
		$(viewRating).removeClass('show').addClass('hidden');
	} else {
		$(viewRating).removeClass('hidden').addClass('show');
		var bookmark = $(this).closest('.bookmark');
		var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
		$.ajax({
			type : 'GET',
			url : '/TagRecommend/network/getRateResult',
			data : {
				'bookmarkID' : bookmarkID,
			},
			success : function (data) {
				console.log(data);
				$(viewRating).html('');
				if(data.status == "SUCCESS") {
					var veryPoorPercent = ((data.result.veryPoorCount / data.result.totalCount)*100).toFixed(2);
					var quitePoorPercent = ((data.result.quitePoorCount / data.result.totalCount)*100).toFixed(2);
					var poorPercent = ((data.result.poorCount / data.result.totalCount)*100).toFixed(2);
					var quiteFairPercent = ((data.result.quiteFairCount / data.result.totalCount)*100).toFixed(2);
					var fairPercent = ((data.result.fairCount / data.result.totalCount)*100).toFixed(2);
					var acceptablePercent = ((data.result.acceptableCount / data.result.totalCount)*100).toFixed(2);
					var quiteGoodPercent = ((data.result.quiteGoodCount / data.result.totalCount)*100).toFixed(2);
					var goodPercent = ((data.result.goodCount / data.result.totalCount)*100).toFixed(2);
					var veryGoodPercent = ((data.result.veryGoodCount / data.result.totalCount)*100).toFixed(2);
					var inner = '<p class="text-center">Rating Result</p><div class="progress">';
					inner += '<div class="progress-bar progress-bar-very-poor" style="width: ' + veryPoorPercent + '%">Very Poor</div>';
					inner += '<div class="progress-bar progress-bar-quite-poor" style="width: ' + quitePoorPercent + '%">Quite Poor</div>';
					inner += '<div class="progress-bar progress-bar-poor" style="width: ' + poorPercent + '%">Poor</div>';
					inner += '<div class="progress-bar progress-bar-quite-fair" style="width: ' + quiteFairPercent + '%">Quite Fair</div>';
					inner += '<div class="progress-bar progress-bar-fair" style="width: ' + fairPercent + '%">Fair</div>';
					inner += '<div class="progress-bar progress-bar-acceptable" style="width: ' + acceptablePercent + '%">Acceptable</div>';
					inner += '<div class="progress-bar progress-bar-quite-good" style="width: ' + quiteGoodPercent + '%">Quite Good</div>';
					inner += '<div class="progress-bar progress-bar-good" style="width: ' + goodPercent + '%">Good</div>';
					inner += '<div class="progress-bar progress-bar-very-good" role="progressbar" style="width: ' + veryGoodPercent + '%">Very Good</div></div>';
					inner += '<p class="text-center">Rating Statistics</p>';
				
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-very-good" style="width: ' + veryGoodPercent + '%">' + veryGoodPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Very Good: ' + data.result.veryGoodCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-good" style="width: ' + goodPercent + '%">' + goodPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Good: ' + data.result.goodCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-quite-good" style="width: ' + quiteGoodPercent + '%">' + quiteGoodPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Quite Good: ' + data.result.quiteGoodCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-acceptable" style="width: ' + acceptablePercent + '%">' + acceptablePercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Acceptable: ' + data.result.acceptableCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-fair" style="width: ' + fairPercent + '%">' + fairPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Fair: ' + data.result.fairCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-quite-fair" style="width: ' + quiteFairPercent + '%">' + quiteFairPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Quite Fair: ' + data.result.quiteFairCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-poor" style="width: ' + poorPercent + '%">' + poorPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Poor: ' + data.result.poorCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-quite-poor" style="width: ' + quitePoorPercent + '%">' + quitePoorPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Quite Poor: ' + data.result.quitePoorCount + '/' + data.result.totalCount + '</div></div>';
					inner += '<div class="row"><div class="col-lg-3"><div class="progress"><div class="progress-bar progress-bar-very-poor" style="width: ' + veryPoorPercent + '%">' + veryPoorPercent + '%' + '</div></div></div>';
					inner += '<div class="col-lg-8"> Very Poor: ' + data.result.veryPoorCount + '/' + data.result.totalCount + '</div></div>';
					$(viewRating).html(inner);
				}
				if(data.status == "EMPTY") {
					var inner = '<div class="text-center"><small class="text-warning">No Rating Result</small></div>';
					$(viewRating).html(inner);
				}
				
			}
		});
	}
});
/* =============================================== */
/* Xử lí khi người dùng clic Add Tag / Edit */
/* =============================================== */
$('#nw-add-tag-modal').on('show.bs.modal', function(e) {
	invoker = $(e.relatedTarget);
});
$('#edit-modal').on('show.bs.modal', function(e) {
	invoker = $(e.relatedTarget);
	getBookmarkTags();
});

$(document).on('click', '.tag-delete', function(e) {
	console.log($(this).parent().text().trim().split('#')[1]);
	var deletedTag = $(this).parent().text().trim().split('#')[1];
	deletedTags.push(deletedTag);
	if($('.dashboard').hasClass('show')) {
		displayTags(taggedTags, deletedTag, $('#tagged-tags'));
	} else if ($('.discover').hasClass('show')) {
		displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'))
	}
});
$(document).on('click', '.tag-restore', function(e) {
	console.log($(this).parent().text().trim().split('#')[1]);
	var restoreTag = $(this).parent().text().trim().split('#')[1];
	deletedTags.splice(deletedTags.indexOf(restoreTag), 1);
	if($('.dashboard').hasClass('show')) {
		displayTags(taggedTags, deletedTag, $('#tagged-tags'));
	} else if ($('.discover').hasClass('show')) {
		displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'))
	}
});

function getBookmarkTags ( ) {
	var bookmarkID = $(invoker).closest('.bookmark').find('input[name="bookmarkID"]').attr('value');
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/dashboard/getBookmarkTags',
		data : {
			'bookmarkID' : bookmarkID
		}, success : function(data) {
			console.log(data);
			taggedTags = data.result;
			displayTags(taggedTags, deletedTags, $('#tagged-tags'));
		}
	}) ;
}
/* =============================================== */
/* Thêm tag vào bookmark của người dùng khác */
/* =============================================== */
$(document).ready(function() {
	$('#nw-add-tag-form').submit(function(e) {
		e.preventDefault();
		if(!$('#nw-tag').val()) {
			$(this).after('<div style="margin-top: 20px;" class="alert alert-danger" role="alert"><i class="fa fa-times"></i> Tag can not be blank</div>');
		} else {
			var tags = $('#nw-tag').val().split(/,\s*/);
			var bookmark = $(invoker).closest('.bookmark');
			var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
			$.ajax({
				type : 'POST',
				url : '/TagRecommend/network/addTags',
				data : {
					'bookmarkID' : bookmarkID,
					'tags' : tags
				},
				traditional : true,
				success : function(data) {
					$('#nw-tag').val('');
					$('$nw-add-tag-modal').modal('hide');
				}
			});
		}
	});
});
/* =============================================== */
/*           Click link tăng số lần view           */
/* =============================================== */
$(document).on('mousedown', '.bookmark-link', function(e) {
	if($('.dashboard').hasClass('hidden')) {
		var bookmark = $(this).closest('.bookmark');
		var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
		var badge = $(bookmark).find('.badge');
		var viewTime = parseInt($(badge).text());
		$(badge).text(viewTime + 1);
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/network/linkClick',
			data : {
				'bookmarkID' : bookmarkID
			},
			success : function(data) {
				
			}
		});	
	}
});
/* =============================================== */
/*                  Xử lí Sort By                  */
/* =============================================== */
$(document).on('click', '#ar', function(e) {
	offset = 0;
	sortBy = 1;
	$(this).html('<i class="fa fa-check-circle"></i> Average Rating');
	$('#vt').text('View Times');
	$('#ct').text('Copy Times');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#vt', function(e) {
	offset = 0;
	sortBy = 2;
	$(this).html('<i class="fa fa-check-circle"></i> View Times');
	$('#ar').text('Average Rating');
	$('#ct').text('Copy Times');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#ct', function(e) {
	offset = 0;
	sortBy = 3;
	
	$(this).html('<i class="fa fa-check-circle"></i> Copy Times');
	$('#vt').text('View Times');
	$('#ar').text('Average Rating');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#pt', function(e) {
	offset = 0;
	sortBy = 4;
	$(this).html('<i class="fa fa-check-circle"></i> Posted Time');
	$('#vt').text('View Times');
	$('#ct').text('Copy Times');
	$('#ar').text('Average Rating');
	
	ajaxSortBy(sortBy);
});
function ajaxSortBy(sortBy) {
	scene.remove();
	if($('.search').hasClass('show')) {
		var ajaxUrl = "/TagRecommend/search";
		var ajaxData = {'sortBy' : sortBy, 'offset' : offset, 'tag' : searchTag};
		var content = $('.page-content').find('.search');
	} else {
		if(document.URL.indexOf('network') > -1) {
			var ajaxUrl = "/TagRecommend/network/getBookmarks";
			var ajaxData = {'sortBy' : sortBy, 'offset' : offset};
			var content = $('.page-content').find('.network');
		} else if ($('.discover').hasClass('show')) {
			var ajaxUrl = "/TagRecommend/discover/discoverBookmarks";
			var ajaxData = {'subscriptionTags' : totalDiscoverTags, 'sortBy' : sortBy, 'offset' : offset};
			var content = $('.discover').eq(1);
		}
	}
	var save = $(content).find('.sort-by');
	$(content).html('');
	$(content).append('<div class="waiting" style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-5x"></i></div>');
	$.ajax({
		type : 'GET',
		url : ajaxUrl,
		traditional : true,
		data : ajaxData,
		success : function(data) {
			console.log(data);
			$(content).html('');
			$(content).append(save);
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				offset += 5;
				infiniteScroll(content);	
			}
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				if(sortBy == 1) {
					inner += 'There are no bookmarks has average rating > 0';
				}
				if(sortBy == 2) {
					inner += 'There are no bookmarks has view times > 0';
				}
				if(sortBy == 3) {
					inner += 'There are no bookmarks has copy times > 0';
				}
				inner += '</p></div></div>';
				$(content).append(inner);
			}
		}
	});
}
/* =============================================== */
/*      Xử lí hiển thỉ Bookmark trả về từ ajax     */
/* =============================================== */
function loadBookmarks(content, data) {
	for (i = 0; i < data.result.length; i++) {
		var bookmark = data.result[i];
		var ratingID = 'input-' + bookmark.bookmarkID;
		console.log('Star Rating' + ratingID);
		var inner = '<div class="bookmark">';
		inner += '<div class="row"><div class="col-lg-12"><h3>' + bookmark.title + '</h3></div></div>';
		inner += '<div class="row"><div class="col-lg-9"><a class="text-info bookmark-link" target="_blank" href="' + bookmark.url + '">' + bookmark.url + '</a></div>';
		inner += '<div class="col-lg-3 text-center"><p>Last Updated: ' + bookmark.date + '</p></div></div>';
		inner += '<div class="row"><div class="col-lg-9"><div class="inline-div">';
		inner += '<i class="fa fa-tags"></i> Tags: ';
		for (j = 0; j < bookmark.tags.length; j++) {
			var tag = bookmark.tags[j];
			var id = 'menu' + '-' + bookmark.bookmarkID + '-' + j;
			inner += '<div class="dropdown inline-div">';
			inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
					+ id + '">#';
			if($(content).hasClass('search') && tag == searchTag) {
				inner += '<b>' + tag + '</b>';
			} else if($(content).hasClass('discover') && totalDiscoverTags.indexOf(tag) > -1) {
				inner += '<b>' + tag + '</b>';
			} else {
				inner += tag;
			}
			inner += '</code>';
			inner += '<ul class="dropdown-menu" role="menu" aria-labelledby="' + id + '">';
			inner += '<input type="hidden" name="tagContent" value="' + tag +'" />';
			inner += '<li role="presentation"><a role="menuitem" class="tag-menu1" tabindex="-1" href="#">Filter by #' + tag + '</a></li>';
			inner += '<li role="presentation"><a role="menuitem" class="tag-menu2" tabindex="-1" href="#"> Find #' + tag + ' on System</a></li>';
			inner += '</ul></div>';
		}
		inner += '</div></div>'; // end col-lg-9 , end inline-div
		inner += '<div class="col-lg-3 text-center"><div class="btn-toolbar">';
		if($(content).hasClass('dashboard')) {
			inner += '<button class="btn btn-sm btn-success" data-toggle="modal" data-target="#edit-modal"><i class="fa fa-pencil-square-o"></i> Edit </button>';
		} else {
			if(bookmark.friend === true) {
				inner += '<button class="btn btn-sm btn-success" data-toggle="modal" data-target="#nw-add-tag-modal"><i class="fa fa-pencil-square-o"></i> Add Tag</button>';
			} else {
				inner += '<button class="btn btn-sm btn-warning"><i class="fa fa-user-plus"></i> Follow</button>';
			}
			if(bookmark.copied === true) {
				inner += '<button class="btn btn-sm btn-info uncopy-bookmark"><i class="fa fa-files-o"></i> Copied (' + bookmark.copyTimes + ')</button>';
			} else {
				inner += '<button class="btn btn-sm btn-success copy-bookmark"><i class="fa fa-files-o"></i> Copy (' + bookmark.copyTimes + ')</button>';	
			}
		}
		inner += '<input type="hidden" name="bookmarkID" value="'+ bookmark.bookmarkID + '">';
		if($(content).hasClass('network') || $(content).hasClass('discover') || $(content).hasClass('search')) {
			inner += '<input type="hidden" name="postedUserID" value="' + bookmark.postedUserID + '" />';	
		}
		inner += '</div></div></div>'; // end div, end col-lg-3,
		// View other Tag
		inner += '<div class="row"><div class="col-lg-12"><a class="view-tags" style="cursor: pointer;"><i class="fa fa-tags"></i> Tags from other users: </a></div></div>';
		inner += '<div class="row"><div class="col-lg-11" style="margin-top: 5px"><div class="jumbotron hidden tags-jumbotron"><div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-2x"></i></div></div></div></div>';
		// Rating
		inner += '<div class="row"><div class="col-lg-1" style = "margin-top: 10px">Rating : </div><div class="col-lg-4 rating-div"><input id="' + ratingID + '" class="rating" data-size="xs"></div>';
		// View count
		inner += '<div class="col-lg-4" style = "margin-top: 10px"><p style="font-size: 150%" class="avg-rating">Avg Rating: ' + bookmark.totalRating.toFixed(2) +'</p></div><div class="col-lg-3 text-center" style="margin-top: 10px">View Count <span class="badge">' + bookmark.viewTimes + '</span></div></div>';
		// View Rating Result
		inner += '<div class="row"><div class="col-lg-9"><a class="view-rating" style="cursor: pointer;"><i class="fa fa-list"></i> View Rating result: </a></div></div>';
		inner += '<div class="row"><div class="col-lg-11" style="margin-top: 5px"><div class="jumbotron hidden rating-jumbotron"><div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-2x"></i></div></div></div></div>'
		inner += '<div class="row"><div class="col-lg-12" style="margin-top: 10px"><div > </div></div></div>';
		if(!$(content).hasClass('dashboard')) {
			inner += '<div class="row"><div class="col-lg-12" style="margin-top: 25px">';
			inner += '<p><i>Posted by: ' + bookmark.firstName + ' ' + bookmark.lastName + '</i></p></div>';
			inner += '</div>';	// end row, 
		}
		inner += '</div>'; // end bookmark
		$(content).append(inner);
		createRatingChoices(ratingID, bookmark.rated);
	}
	if($(content).find('.bookmarks').length == 0) {
		$(content).find('.bookmark').wrapAll('<div class="bookmarks">');
	} else {
		var save = $(content).find('.bookmarks').html();
		save += inner;
	}
}

/* =============================================== */
/*          Auto Complete text khi add tag         */
/* =============================================== */
$(document).ready(function() {
	function split(val) {
		return val.split( /,\s*/ );
    }
    function extractLast(term) {
      return split( term ).pop();
    }
    $('#edit').autocomplete({
		source : function(request, response) {
			response($.ui.autocomplete.filter(availableTags, extractLast(request.term)));
		},
		focus : function() {
			return false;
		},
		select : function(event, ui) {
			var terms = split(this.value);
			terms.pop();
			terms.push(ui.item.value);
			terms.push("");
			this.value = terms.join(", ");
			return false;
		}
	});
	$('#nw-tag').autocomplete({
		source : function(request, response) {
			response($.ui.autocomplete.filter(availableTags, extractLast(request.term)));
		},
		focus : function() {
			return false;
		},
		select : function(event, ui) {
			var terms = split(this.value);
			terms.pop();
			terms.push(ui.item.value);
			terms.push("");
			this.value = terms.join(", ");
			return false;
		}
	});
	$('#subscription-tag').autocomplete({
		source : function(request, response) {
			response($.ui.autocomplete.filter(availableTags, extractLast(request.term)));
		},
		focus : function() {
			return false;
		},
		select : function(event, ui) {
			var terms = split(this.value);
			terms.pop();
			terms.push(ui.item.value);
			terms.push("");
			this.value = terms.join(", ");
			return false;
		}
	});
	/* Sửa lại hàm filter của autocomplete */
	$.ui.autocomplete.filter = function(array, term) {
		var matcher = new RegExp("^"+ $.ui.autocomplete.escapeRegex(term), "i");
		return $.grep(array, function(value) {
			return matcher.test(value.label|| value.value|| value);
		});
	};
});
/* =============================================== */
/*            Người dùng click chọn tag            */
/* =============================================== */
$(document).on('click', '.tag-menu1', function(e) {
	var menu = $(this).closest('ul');
	var tag = $(menu).find('input[name="tagContent"]').attr('value');
	console.log('Filter tag: ' + tag);
	if($('.search').hasClass('show')) {
		filterTag(this, tag, 'search');
		console.log('In Search Filter');
	} else {
		if(document.URL.indexOf('discover') > -1) {
			filterTag(this, tag, 'discover');
		}
		if(document.URL.indexOf('network') > -1) {
			filterTag(this, tag, 'network');
		}
		if(document.URL.indexOf('dashboard') > -1) {
			filterTag(this, tag, 'dashboard');
		}	
	}
});

$(document).on('click', '.tag-menu2', function(e) {
	offset = 0;
	sortBy = 4;
	scene.remove();
	var menu = $(this).closest('ul');
	searchTag = $(menu).find('input[name="tagContent"]').attr('value');
	$('#search-input').val('#' + searchTag);
	$('#search-input').focus();
	if(document.URL.indexOf('discover') > -1) {
		$('.discover').removeClass('show').addClass('hidden');
	}
	if(document.URL.indexOf('network') > -1) {
		$('.network').removeClass('show').addClass('hidden');
		$('.network').eq(1).html('');
	}
	if(document.URL.indexOf('dashboard') > -1) {
		$('.dashboard').removeClass('show').addClass('hidden');
		$('dashboard').html('');
		
		
	}
	if($('.search').hasClass('hidden')) {
		$('.search').removeClass('hidden').addClass('show');
	} else {
		$('.search').eq(1).html('');	
	}
	$('.search').first().html('<div class="row"><div class="col-lg-12"><h3 class="text-center text-info">Search Result for: #' + searchTag + '</h3></div></div>');
	
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/search',
		data : {
			'sortBy' : sortBy,
			'tag' : searchTag,
			'offset' : offset
		}, success : function(data) {
			var searchContent = $('.search').eq(1);
			$(searchContent).html('<div class="row sort-by" style="padding-bottom: 18px; border-bottom: 1px solid #EEE;">' 
					+ '<div class="col-lg-2"><h4 class="text-info"><i class="fa fa-sort"></i> Sort By:</h4></div><div class="col-lg-2"><button id="ar" class="btn btn-info btn-block">Average Rating</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="vt" class="btn btn-info btn-block">View Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="ct" class="btn btn-info btn-block">Copy Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="pt" class="btn btn-info btn-block">Posted Time</button</div></div>');
			switch(sortBy) {
			case 1: $('#ar').html('<i class="fa fa-check-circle"></i> Average Rating'); break;
			case 2: $('#vt').html('<i class="fa fa-check-circle"></i> View Times'); break;
			case 3: $('#ct').html('<i class="fa fa-check-circle"></i> Copy Times'); break;
			case 4: $('#pt').html('<i class="fa fa-check-circle"></i> Posted Time'); break;
			}
			if(data.status == "SUCCESS") {
				loadBookmarks(searchContent, data);
				$(searchContent).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				offset += 5;
				infiniteScroll(searchContent);
			}
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				inner += 'No bookmarks has tagged with #' + searchTag;
				inner += '</p></div></div>';
				$(searchContent).append(inner);
			}
		}
	});
});

function filterTag(element, tag, current) {
	var content = $(element).closest('.' + current);
	var bookmarks = $(content).find('.bookmark');
	var filteredBookmarks = [];
	for(i=0; i<bookmarks.length; i++) {
		var isTagged = false;
		var bookmark = bookmarks[i];
		var inputTags = $(bookmark).find('input[name="tagContent"]');
		for(j=0; j<inputTags.length; j++) {
			var inputTag = inputTags[j];
			if($(inputTag).attr('value') == tag) {
				isTagged = true;
				var ratingID = $(bookmark).find('.rating').attr('id');
				$('#' + ratingID).rating('destroy');
				filteredBookmarks.push(bookmark);
			}
		}
	}
	if(current == 'network' || current == 'search') {
		var save = $(content).find('.sort-by');
	}
	var bookmarks = $(content).find('.bookmarks');
	scene.remove();
	$(content).html('');
	if(current == 'network' || current == 'search') {
		$(content).html(save);
	}
	$(bookmarks).html('');

	for(i=0; i<filteredBookmarks.length; i++) {
		$(bookmarks).append(filteredBookmarks[i]);
	}
	$(content).append(bookmarks);
	for(i=0; i<filteredBookmarks.length; i++) {
		var bookmarkID = $(filteredBookmarks[i]).find('input[name="bookmarkID"]').attr('value');
		var id = $(filteredBookmarks[i]).find('.rating').attr('id');
		$.ajax({
			url : '/TagRecommend/dashboard/getTotalRating',
			type : 'GET',
			async: false,  
			data: {
				'bookmarkID' : bookmarkID
			}, success : function (data) {
				console.log(data);
				createRatingChoices(id, data.result);
			}
			
		});
	}
	
	$(content).append('<div class="row"><div class="col-lg-12"><p class="text-center text-warning"> ' + '#' + tag + ' Filtered ' + '</p></div></div>');
}
/* =============================================== */
/*           Người dùng click chọn edit            */
/* =============================================== */
$(document).ready(function() {
	$('#edit-form').submit(function(e) {
		$('#tagged-tags').html('<div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-5x"></i></div>');
		e.preventDefault();
		var tags = $('#edit').val().split(/,\s*/);
		tags.splice(tags.length - 1, 1);
		var bookmark = $(invoker).closest('.bookmark');
		var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/dashboard/editBookmark',
			data : {
				'bookmarkID' : bookmarkID,
				'addedTags' : tags,
				'deletedTags' : deletedTags
			},
			traditional : true,
			success : function(data) {
				$('#edit').val('');
				getBookmarkTags();
				var codes = $(invoker).closest('.bookmark').find('code');
				if(deletedTags.length != 0) {
					for(i=0; i<codes.length; i++) {
						for(j=0; j<deletedTags.length; j++) {
							if($(codes[i]).text().indexOf(deletedTags[j]) > -1) {
								$(codes[i]).remove();
							}
						}
					}
				}
			}
		});
	});
});
/* =============================================== */
/*        Copy Bookmark từ người dùng khác         */
/* =============================================== */
$(document).on('click', '.copy-bookmark', function(e) {
	$(this).removeClass('copy-bookmark').addClass('uncopy-bookmark');
	$(this).removeClass('btn-success').addClass('btn-info');
	var element = this;
	var bookmark = $(this).closest('.bookmark');
	var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
	e.preventDefault();
	$.ajax({
		type : 'POST',
		url : '/TagRecommend/network/copyBookmark',
		data : {
			'bookmarkID' : bookmarkID
		}, success : function(data) {
			if(data.status == "SUCCESS") {
				$(element).text('Copied (' + data.result + ')');	
			}
		}
	});
});
$(document).on('click', '.uncopy-bookmark', function(e) {
	$(this).removeClass('uncopy-bookmark').addClass('copy-bookmark');
	$(this).removeClass('btn-info').addClass('btn-success');
	var element = this;
	var bookmark = $(this).closest('.bookmark');
	var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
	e.preventDefault();
	$.ajax({
		type : 'POST',
		url : '/TagRecommend/network/uncopyBookmark',
		data : {
			'bookmarkID' : bookmarkID
		}, success : function(data) {
			if(data.status == 'SUCCESS') {
				$(element).text('Copy (' + data.result + ')');	
			}
		}
	});
});

/* =============================================== */
/* Discover các Bookmark theo default subscription */
/* =============================================== */
$(document).ready(function(e) {
	$('#subscription-form').submit(function(e) {
		$('#subscription-tags-modal').html('<div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-2x"></i></div>');
		var tags = $('#subscription-tag').val().split(/,\s*/);
		tags.splice(tags.length - 1, 1);
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/discover/editSubscription',
			data : {
				'subscriptionTags' : tags,
				'deletedTags' : deletedTags
			},
			traditional : true,
			success : function(data) {
				$('#subscription-tag').val('');
				getSubscriptionTags();
				var inner = 'Your subscription tags:  ';
				for (i = 0; i < subscriptionTags.length; i++) {
					inner += '<span><code>#' + subscriptionTags[i] + '</code></span>';
				}
				$('#subscription-tags').html(inner);
			}
		});
	});
});

$('#subscription-modal').on('show.bs.modal', function(e) {
	if(subscriptionTags.length == 0) {
		$('#subscription-tags-modal').html('<p class="text-center text-warning">You did not set any tag subscription yet! Please set some tags</p>');
	} else {
		displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'));
	}
});

function getSubscriptionTags ( ) {
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/discover/subscription',
		success : function(data) {
			console.log(data);
			subscriptionTags = data.result[1];
			defaultSubscriptionTags = data.result[0];
			if(subscriptionTags.length == 0) {
				$('#subscription-tags-modal').html('<p class="text-center text-warning">You did not set any tag subscription yet! Please set some tags</p>');
			} else {
				displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'));
			}
		}
	}) ;
}

function displayTags(tags, deletedTags, element) {
	var inner = '';
	var from = 0;
	while(from < tags.length) {
		var to = from + 4;
		if(to > tags.length) {
			to = from + (tags.length - from);
		}
		inner += '<div class="row">';
		for(i=from; i<to; i++) {
			var isDeleted = false;
			for(j=0; j<deletedTags.length; j++){
				if(tags[i] == deletedTags[j]) {
					isDeleted = true;
				}
			}
			if(isDeleted === true) {
				inner += '<div class="col-lg-3 text-center"><del>#' + tags[i] + ' <i class="fa fa-tag tag-restore"></i></del></div>';
			} else {
				inner += '<div class="col-lg-3 text-center"><code>#' + tags[i] + ' <i class="fa fa-times tag-delete"></i></code></div>';	
			}
		}	
		inner += '</div>';
		from = to;
	}
	$(element).html(inner);
}

function discoverBookmarks(tags) {
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/discover/discoverBookmarks',
		traditional : true,
		data : {
			'subscriptionTags' : tags,
			'offset' : offset,
			'sortBy' : sortBy
		},
		success : function(data) {
			console.log(data);
			var content = $('.discover').eq(1);
			$(content).html('<div class="row sort-by" style="padding-bottom: 18px; border-bottom: 1px solid #EEE;">' 
					+ '<div class="col-lg-2"><h4 class="text-center"><i class="fa fa-sort"></i> Sort By:</h4></div><div class="col-lg-2"><button id="ar" class="btn btn-primary btn-block">Average Rating</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="vt" class="btn btn-primary btn-block">View Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="ct" class="btn btn-primary btn-block">Copy Times</button></div>' 
					+ '<div class="col-lg-2 sort-method"><button id="pt" class="btn btn-primary btn-block">Posted Time</button</div></div>');
			switch(sortBy) {
			case 1: $('#ar').html('<i class="fa fa-check-circle"></i> Average Rating'); break;
			case 2: $('#vt').html('<i class="fa fa-check-circle"></i> View Times'); break;
			case 3: $('#ct').html('<i class="fa fa-check-circle"></i> Copy Times'); break;
			case 4: $('#pt').html('<i class="fa fa-check-circle"></i> Posted Time'); break;
			}
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				if(sortBy == 1) {
					inner += 'There are no bookmarks has average rating > 0';
				}
				if(sortBy == 2) {
					inner += 'There are no bookmarks has view times > 0';
				}
				if(sortBy == 3) {
					inner += 'There are no bookmarks has copy times > 0';
				}
				inner += '</p></div></div>';
				$(content).append(inner);
			}	
		}
	});
}
