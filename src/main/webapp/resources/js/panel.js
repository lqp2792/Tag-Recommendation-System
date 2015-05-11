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
/*        Thay đổi page content, page header       */
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
	if(document.URL.indexOf('settings') > -1) {
		accessSettingsPage();
	}
	/* =============================================== */
	/*   Thực hiện chức năng search khi gõ vào input   */
	/* =============================================== */
	$('#search-button').click(function(e){
		offset = 0;
		processSearch();
	})
	$('#search-input').keypress(function (e) {
	  if (e.which == 13) {
	    $('#search-button').click();
	    return false;  
	  }
	});
	/* =============================================== */
	/*          Auto cập nhật số lương online          */
	/* =============================================== */
	setTimeout(updateOnlineUsers, 1000);
	/* =============================================== */
	/*      Click Follow / Unfolow người dùng khác     */
	/* =============================================== */
	$(document).on('click', '.follow', function(e) {
		$(this).html('<i class="fa fa-spinner fa-spin"></i> Processing...');
		follow(this);
	});
	/* =============================================== */
	/*                 Xử lí nhập Feedback             */
	/* =============================================== */
	$('#feedback-form').submit(function(e) {
		e.preventDefault();
		processFeedback();
	});
	/* =============================================== */
	/*                  Change password                */
	/* =============================================== */
	$('#change-password').click(function(e) {
		$('.change-password-div').removeClass('hide').addClass('show');
	});
	$('#change-password-form').submit(function(e) {
		e.preventDefault();
		$(this).find('button').html('<i class="fa fa-spinner fa-spin"></i> Processing');
		processChangePasswordForm();
	});
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
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> Loading...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 
			if(data.status == "EMPTY") {
				var inner = '<div class="row" style="margin-top: 25px"><div class="col-lg-12"><p class="text-center text-warning">';
				inner += 'You did not post any bookmarks. Please post your most interested bookmarks!';
				inner += '</p></div></div>';
				$(content).html(inner);
			}	
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate!</h4>')
		}
	}) ;
	if (localStorage.getItem('isGuided') === null) {
		 localStorage.setItem('isGuided', false);
	}
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/dashboard/history',
		success : function(data) {
			if(data.result.onlineCount == 0 && localStorage.isGuided == 'false') {
				bootbox.alert('<h4 class="text-center text-info">Welcome to my social bookmarking system </h4>' + 
						'<p class="text-center text-info">Press OK to take a tour to system features</p>', function(e) {
					 var introguide = introJs();
					 introguide.setOptions({
						    steps: [
						        {
						          element: '#my-bookmarks-menu',
						          intro: 'By default, you will be here. This is where you can manage all of your posted Bookmark',
						          position: 'right'
						        },
						        {
						          element: '#network-menu',
						          intro: 'By Click here, You can follow other users that were recommend by system, and check their newest Bookmark',
						          position: 'right'
						        },
						        {
						          element: '#discover-menu',
						          intro: 'You can choose some Tags, and System will find all Bookmark has that Tag for you. By default, system will use your most used Tags to discover',
						          position: 'right'
						        },
						        {
						          element: '#trending-menu',
						          intro: 'Click here, You can see top 10 most used Tags of system, and Top 15 most interested Bookmark of System',
						          position: 'right'
						        },
						        {
						          element: '#add-bookmark-menu',
						          intro: 'The most important part of System, You post a Bookmark and annotate with Recommended Tags',
						          position: 'right'
						        },
						        {
						          element: '#setting-menu',
						          intro: 'Click here, you can change your password, view system users',
						          position: 'right'
						        }
						    ]
						});
					 introguide.start();
					 localStorage.setItem('isGuided', true);
				});
			} 
			if (data.result.onlineCount >  0 ){
				$('.text-info').eq(0).after('<h4>Last Online: ' + data.result.lastedOnline + '</h4>');	
			}
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate!</h4>')
		}
	});
}

function accessNetworkPage() {
	$('.dashboard').removeClass('show').addClass('hidden');
	$('.network').removeClass('hidden').addClass('show');
	$.ajax({
		type : 'GET',
		url : '/TagRecommend/network/getBookmarks',
		data : {
			'sortBy' : sortBy,
			'offset' : offset
		}, success : function(data) {
			console.log(data);
			var content = $('.network').eq(1);
			loadSortByBar(content, sortBy);
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> Loading...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
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
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
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
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
		}
	});
}

function accessSettingsPage() {
	$('.dashboard').removeClass('show').addClass('hidden');	
	$('.settings').removeClass('hidden').addClass('show');
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
				loadSortByBar(content, sortBy);
				loadBookmarks(content, data);
				if ((data.result.length % 5) == 0) {
					$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> Loading...</div>');
					offset += 5;
				} else {
					if (typeof scene !== 'undefined') {
					    scene.remove();
					}
					$(content).append('<div class="center-div"><h4><a href="#" id="backToTop"><i class="fa fa-reply"></i> There is no more Bookmarks</a></h4></div>');
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
				var rating = $(bookmark).find('.avg-rating');
				$(rating).text('Avg Rate: ' + data.result.toFixed(2));
				$(rating).textillate({
					autoStart: false,
					'in' : {effect : 'pulse'}
				})
				$(bookmark).find('.avg-rating').textillate('in');
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
		loadOtherTags(this);
	}
});

function loadOtherTags(element) {
	var viewTag = $(element).closest('.bookmark').find('.tags-jumbotron');
	var bookmark = $(element).closest('.bookmark');
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
				$(viewTag).html('');
				for(i=0; i<data.result.length; i++) {
					var userID = $('.navbar-header').find('.userID').attr('value');
					var additionalTag = data.result[i];
					var inner = '<div class="row">';
					inner += '<div class="col-lg-2">';
					if(additionalTag.userID == userID) {
						inner += 'You: </div>';
					} else {
						inner += additionalTag.firstName + ' ' + additionalTag.lastName + ': </div>';
					}
					inner += '<div class="col-lg-10"><div class="inline-div">';
					for (j = 0; j < additionalTag.tags.length; j++) {
						var tag = additionalTag.tags[j];
						var id = 'menu' + '-' + additionalTag.userID + '-' + bookmarkID + '-' + j;
						inner += '<div class="dropdown inline-div">';
						if(additionalTag.userID == userID) {
							inner += '<i class="fa fa-times othertag-delete" style="cursor: pointer;"></i><code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
								+ id + '">#' + tag + '</code> ';
						} else {
							inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
								+ id + '">#' + tag + '</code>';
						}
						
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
/* =============================================== */
/*               Xóa other tag on click            */
/* =============================================== */
$(document).on('click', '.othertag-delete', function(e) {
	var bookmarkID = $(this).closest('.bookmark').find('input[name="bookmarkID"]').attr('value');
	var otherTag = $(this).closest('.dropdown').find('code').text().slice(1);
	$.ajax({
		type : 'POST',
		url : '/TagRecommend/network/deleteOtherTag',
		data : {
			'bookmarkID' : bookmarkID,
			'otherTag' : otherTag
		}, success : function(data) {
			if(data.status == "SUCCESS") {
				bootbox.alert('<h4 class="text-center text-info"><i class="fa fa-check-square-o"></i> Deleted Tags: <code>#' + otherTag + '</code> successfully!</h4>');
			} else {
				bootbox.alert('<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i>' + data.result + '</h4>');
			}
		}, error: function(xhr, textStatus, error) {
			console.log(xhr.statusText);
			console.log(textStatus);
			console.log(error);
			bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate!</h4>')
		}
	});
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
	if ($('.discover').hasClass('show')) {
		displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'))
	} else {
		displayTags(taggedTags, deletedTags, $('#tagged-tags'));
	}
});
$(document).on('click', '.tag-restore', function(e) {
	console.log($(this).parent().text().trim().split('#')[1]);
	var restoreTag = $(this).parent().text().trim().split('#')[1];
	deletedTags.splice(deletedTags.indexOf(restoreTag), 1);
	if ($('.discover').hasClass('show')) {
		displayTags(subscriptionTags, deletedTags, $('#subscription-tags-modal'))
	} else {
		displayTags(taggedTags, deletedTags, $('#tagged-tags'));
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
			tags.splice(tags.length - 1, 1);
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
					if(data.status == "SUCCESS") {
						var message = '';
						for(i=0; i<tags.length; i++) {
							message += '<code>' + tags[i] + '</code>  '; 
						}
						bootbox.alert('<h4 class="text-center text-info"><i class="fa fa-check-square-o"></i> Added Tags: ' + message + ' successfully!</h4>');
						$('#nw-tag').val('');
						$('$nw-add-tag-modal').modal('hide');
					} else {
						bootbox.alert('<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i>' + data.result + '</h4>');
					}
					
				}
			});
		}
	});
});
/* =============================================== */
/*           Click link tăng số lần view           */
/* =============================================== */
$(document).on('mousedown', '.bookmark-link', function(e) {
	var bookmark = $(this).closest('.bookmark');
	var bookmarkID = bookmark.find('input[name="bookmarkID"]').attr('value');
	var badge = $(bookmark).find('.badge');
	$.ajax({
		type : 'POST',
		url : '/TagRecommend/network/linkClick',
		data : {
			'bookmarkID' : bookmarkID
		},
		success : function(data) {
			$(badge).text(data.result);
			$(badge).textillate({
				'in' : {
		            effect: 'rollIn'
		        }
			});
		}
	});	
});
/* =============================================== */
/*                  Xử lí Sort By                  */
/* =============================================== */
$(document).on('click', '#ar', function(e) {
	offset = 0;
	sortBy = 1;
	$(this).html('<i class="fa fa-check-circle"></i> Average Rating');
	$('#vt').text('View Times');
	$('#st').text('Same Tags');
	$('#ct').text('Copy Times');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#st', function(e) {
	offset = 0;
	sortBy = 2;
	$(this).html('<i class="fa fa-check-circle"></i> Same Tags');
	$('#ar').text('Average Rating');
	$('#vt').text('View Times');
	$('#ct').text('Copy Times');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#vt', function(e) {
	offset = 0;
	sortBy = 3;
	$(this).html('<i class="fa fa-check-circle"></i> View Times');
	$('#st').text('Same Tags');
	$('#ar').text('Average Rating');
	$('#ct').text('Copy Times');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#ct', function(e) {
	offset = 0;
	sortBy = 4;
	$(this).html('<i class="fa fa-check-circle"></i> Copy Times');
	$('#vt').text('View Times');
	$('#st').text('Same Tags');
	$('#ar').text('Average Rating');
	$('#pt').text('Posted Time');
	ajaxSortBy(sortBy);
});
$(document).on('click', '#pt', function(e) {
	offset = 0;
	sortBy = 5;
	$(this).html('<i class="fa fa-check-circle"></i> Posted Time');
	$('#vt').text('View Times');
	$('#st').text('Same Tags');
	$('#ct').text('Copy Times');
	$('#ar').text('Average Rating');
	ajaxSortBy(sortBy);
});
function ajaxSortBy(sortBy) {
	if (typeof scene !== 'undefined') {
	    scene.remove();
	}
	if($('.search').hasClass('show')) {
		var ajaxUrl = "/TagRecommend/network/search";
		var searchInput = $('#search-input').val() ;
		var ajaxData = {'searchInput' : searchInput, 'sortBy' : sortBy};
		var content = $('.search').eq(1);
	} else {
		if(document.URL.indexOf('network') > -1) {
			var ajaxUrl = "/TagRecommend/network/getBookmarks";
			var ajaxData = {'sortBy' : sortBy, 'offset' : offset};
			var content = $('.network').eq(1);
		} else if ($('.discover').hasClass('show')) {
			var ajaxUrl = "/TagRecommend/discover/discoverBookmarks";
			var ajaxData = {'subscriptionTags' : totalDiscoverTags, 'sortBy' : sortBy, 'offset' : offset};
			var content = $('.discover').eq(1);
		}
	}
	$(content).html('');
	$(content).append('<div class="waiting" style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-5x"></i></div>');
	$.ajax({
		type : 'GET',
		url : ajaxUrl,
		traditional : true,
		data : ajaxData,
		success : function(data) {
			console.log(data);
			loadSortByBar(content, sortBy);
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				if(!content.hasClass('search')) {
					$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
					offset += 5;
					infiniteScroll(content);	
				}
			
			}
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
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
		if(!$(content).hasClass('dashboard')) {
			inner += '<div class="row"><div class="col-lg-12"><h3>[<span class="text-info">Point: ' + bookmark.point.toFixed() + '</b></span>] ' + bookmark.title + '</h3></div></div>';
		} else {
			inner += '<div class="row"><div class="col-lg-12"><h3><span class="stt">' + (i+offset+1) + '</span> ' + bookmark.title + '</h3></div></div>';	
		}
		inner += '<div class="row"><div class="col-lg-9"><a class="bookmark-link" target="_blank" href="' + bookmark.url + '">' + bookmark.url + '</a></div>';
		inner += '<div class="col-lg-3 text-center"><p><b>Last Updated: ' + bookmark.date + '</b></p></div></div>';
		inner += '<div class="row"><div class="col-lg-9"><div class="inline-div">';
		inner += '<i class="fa fa-tags"></i> Tags: ';
		for (j = 0; j < bookmark.tags.length; j++) {
			var tag = bookmark.tags[j];
			var id = 'menu' + '-' + bookmark.bookmarkID + '-' + j;
			inner += '<div class="dropdown inline-div">';
			inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
					+ id + '">#';
			if($(content).hasClass('search') && tag == searchTag) {
				inner += '<b style="font-size: 14px">' + tag + '</b>';
			} else if($(content).hasClass('discover') && totalDiscoverTags.indexOf(tag) > -1) {
				inner += '<b style="font-size: 14px">' + tag + '</b>';
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
				inner += '<button class="btn btn-sm btn-warning follow"><i class="fa fa-user-plus"></i> Follow</button>';
			}
			if(bookmark.copied === true) {
				inner += '<button class="btn btn-sm btn-info uncopy-bookmark"><i class="fa fa-files-o"></i> Copied (' + bookmark.copyTimes + ')</button>';
			} else {
				inner += '<button class="btn btn-sm btn-success copy-bookmark"><i class="fa fa-files-o"></i> Copy (' + bookmark.copyTimes + ')</button>';	
			}
		}
		inner += '<input type="hidden" name="bookmarkID" value="'+ bookmark.bookmarkID + '">';
		if($(content).hasClass('network') || $(content).hasClass('discover') || $(content).hasClass('search')) {
			inner += '<input type="hidden" class="userID" name="postedUserID" value="' + bookmark.postedUserID + '" />';	
		}
		inner += '</div></div></div>'; // end div, end col-lg-3,
		// View other Tag
		inner += '<div class="row"><div class="col-lg-12"><a class="view-tags" style="cursor: pointer;"><i class="fa fa-tags"></i> Tags from other users: </a></div></div>';
		inner += '<div class="row"><div class="col-lg-11" style="margin-top: 5px"><div class="jumbotron hidden tags-jumbotron"><div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-2x"></i></div></div></div></div>';
		// Rating
		inner += '<div class="row"><div class="col-lg-2" style = "margin-top: 10px">Your Rating : </div><div class="col-lg-4 rating-div"><input id="' + ratingID + '" class="rating" data-size="xs"></div>';
		// View count
		inner += '<div class="col-lg-3" style = "margin-top: 7px"><p style="font-size: 150%" class="avg-rating">Average Rating: ' + bookmark.totalRating.toFixed(2) +'</p></div><div class="col-lg-3 text-center" style="margin-top: 10px">View Count <span class="badge">' + bookmark.viewTimes + '</span></div></div>';
		// View Rating Result
		inner += '<div class="row"><div class="col-lg-9"><a class="view-rating" style="cursor: pointer;"><i class="fa fa-list"></i> View Rating Result: </a></div>';
		inner += '</div>';
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
    	source: function(request, response) {
	        $.ajax ({
	        	type: 'GET',
	        	url: "/TagRecommend/dashboard/availableTags",
	        	data: {
	        		'term': extractLast(request.term)
	        	},
	        	success: function( data ) {
	        		response($.ui.autocomplete.filter(data.result, extractLast(request.term)));
	        	}
        	});
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
		source: function(request, response) {
	        $.ajax ({
	        	type: 'GET',
	        	url: "/TagRecommend/dashboard/availableTags",
	        	data: {
	        		'term': extractLast(request.term)
	        	},
	        	success: function( data ) {
	        		response($.ui.autocomplete.filter(data.result, extractLast(request.term)));
	        	}
        	});
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
				$(searchContent).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> Loading...</div>');
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
	$(this).html('<i class="fa fa-spinner fa-spin"></i> Processing...');
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
				$(element).removeClass('btn-success').addClass('btn-info');
				$(element).text('Copied (' + data.result + ')');	
			}
		}
	});
});
$(document).on('click', '.uncopy-bookmark', function(e) {
	$(this).removeClass('uncopy-bookmark').addClass('copy-bookmark');
	$(this).html('<i class="fa fa-spinner fa-spin"></i> Processing...');
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
				$(element).removeClass('btn-info').addClass('btn-success');
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
			loadSortByBar(content, sortBy);
			if(data.status == "SUCCESS") {
				loadBookmarks(content, data);
				$(content).append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> Loading...</div>');
				offset += 5;
				infiniteScroll(content);	
			} 	
		}
	});
}
/* =============================================== */
/*        Xử lí Search khi gõ trực tiếp vào        */
/* =============================================== */
function processSearch() {
	$('.search').eq(1).html('<div style="text-align: center;"><i class="fa fa-spinner fa-pulse fa-5x"></i></div>');
	if(!$('#search-input').val()) {
		bootbox.alert({
			size: 'small',
			message : '<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i> Search cannot be blank</h4>',
			callback : function () {
				$('#search-input').focus();	
			}
		});
	} else {
		var searchInput = $('#search-input').val() ;
		$.ajax({
			type : 'GET',
			url : '/TagRecommend/network/search',
			data : {
				'searchInput' : searchInput, 'sortBy' : sortBy
			}, success : function(data) {
				if(data.status == "SUCCESS") {
					if($('.discover').hasClass('show')) {
						$('.discover').removeClass('show').addClass('hidden');
						$('.discover').html('');
					}
					if($('.network').hasClass('show')) {
						$('.network').removeClass('show').addClass('hidden');
						$('.network').html('');
					}
					if($('.dashboard').hasClass('show')) {
						if (typeof scene !== 'undefined') {
						    scene.remove();
						}
						$('.dashboard').removeClass('show').addClass('hidden');
						$('dashboard').html('');
					}
					if($('.search').hasClass('hidden')) {
						$('.search').removeClass('hidden').addClass('show');
					} else {
						$('.search').html('');	
					}
					if(searchInput.charAt(0) == '@') {
						processUserSearchResult(data, searchInput);
					}
					if(searchInput.charAt(0) == '#') {
						processTagSearchResult(data, searchInput);
					}
				} else {
					bootbox.alert('<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i>' + data.result + '</h4>');
				}
			}
		});
	}
}
/* =============================================== */
/*              Xủ lí search người dùng            */
/* =============================================== */
function processTagSearchResult(data, searchInput) {
	searchTag = searchInput.slice(1);
	$('.search').eq(0).html('<div class="row"><div class="col-lg-12"><h3 class="text-center text-info">Search Tag: ' + searchInput + '</h3></div></div>');
	if(data.result.length == 0) {
		$('.search').eq(1).html('<div class="row" style="margin-top: 25px"><div class="col-lg-12"><h4 class="text-center text-warning">No Bookmark was found with Tag: ' + searchInput + '</h4></div></div>')
	} else {
		loadSortByBar($('.search').eq(1), sortBy);
		loadBookmarks($('.search').eq(1), data);
	}
}
/* =============================================== */
/*              Xủ lí search người dùng            */
/* =============================================== */
function processUserSearchResult(data, searchInput) {
	var canvasID = null;
	var mostUsedTags = [];
	var tagListID = null;
	$('.search').eq(0).html('<div class="row"><div class="col-lg-12"><h3 class="text-center text-info">Search User: ' + searchInput + '</h3></div></div>');
	if(data.result.length == 0) {
		$('.search').eq(1).html('<div class="row" style="margin-top: 25px"><div class="col-lg-12"><h4 class="text-center text-warning">No user was found for' + searchInput + '</h4></div></div>')
	} else {
		var content = $('.search').eq(1);
		content.html('');
		for(i=0; i<data.result.length; i++) {
			var user = data.result[i];
			mostUsedTags = user.mostUsedTags;
			var inner = '<div class="row user user-item">';
			inner += '<div class="col-lg-2 text-center">'
			inner += '<img src="images/user.png" class="img-thumbnail" alt="User Default Avatar" style="min-height: 100px; height: 100px;">';
			inner += '<input type="hidden" name="userID" class="userID" value=' + user.userID + '" />';
			if(user.followed === false) {
				inner += '<button class="btn btn-sm btn-warning follow" style="margin-top: 10px"><i class="fa fa-user-plus"></i> Follow</button>';
			} else {
				inner += '<button class="btn btn-sm btn-success follow" style="margin-top: 10px"><i class="fa fa-user-times"></i> Unfollow</button>';
			}
			inner += '</div>'; // end avatar image
			inner += '<div class="col-lg-5">';
			inner += '<h2>' + user.firstName + ' ' + user.lastName + '</h2>';
			inner += '<h4 class="text-info">' + user.bookmarkCount + ' - Bookmark  ' + user.followingCount + ' -  Following ' + user.followerCount + ' - Follower</h4>';
			if(user.online === true) {
				inner += '<h4 class="text-info">Status :    <i class="fa fa-circle text-success"></i></h4>';
			} else {
				inner += '<h4 class="text-info">Status :    <i class="fa fa-circle text-danger"></i></h4>';
			}
			
			inner += '</div>';
			inner += '<div class="col-lg-5 text-center">';
			canvasID = 'canvas-' + user.userID;
			tagListID = 'tagList-' + user.userID;
			inner += '<h4>Top Most Used Tags</h4>';
			inner += '<div id="' + canvasID + 'Container"><canvas width="250" height="100" id="' + canvasID + '" class="custom-canvas"></canvas>';	 
			inner += '<ul id="' + tagListID +'">';
			for(j=0; j<mostUsedTags.length; j++) {
				var tag = mostUsedTags[j];
				if(tag.weight < 5) {
					inner += '<li><a href="#" style="font-size: 10px">' + tag.tag +'</a></li>';
				}
				if(tag.weight >= 5 && tag.weight < 10) {
					inner += '<li><a href="#" style="font-size: 17px">' + tag.tag +'</a></li>';
				}
				if(tag.weight >= 10) {
					inner += '<li><a href="#" style="font-size: 24px">' + tag.tag +'</a></li>';
				}
			}
			inner += '</ul>';
			inner += '</div>' //end canvas
			inner += '</div>'; //end col-5
			inner += '</div>' //end user 
			content.append(inner);
			createTagsCanvas($('#' + canvasID + 'Container'), $('#' + canvasID), tagListID);
		}
	}
}
/* =============================================== */
/*   Khởi tạo canvas cho mỗi user khi search user  */
/* =============================================== */
function createTagsCanvas(container, element, tagList) {
	if(!$(element).tagcanvas({
		textColour : '#0066CC',
		outlineColour : '#ff9999',
		maxSpeed : 0.03,
		textHeight: 20,
		dragControl: true,
		noTagsMessage : false,
		depth : 0.99,
		weight : true,
		noSelect : true,
	}, tagList)) {
		$(container).hide();
	}
}
/* =============================================== */
/*                Update Online Users              */
/* =============================================== */
function updateOnlineUsers() {
	$.ajax({
		type : 'GET',
		url: '/TagRecommend/network/updateOnlineUsers',
		success: function(data) {
			$('#network-menu').html('<i class="fa fa-users fa-fw"></i> Network (Online Users: ' + data.result +'  <i class="fa fa-user"></i>)');
	    }
	});
	setTimeout(updateOnlineUsers, 60000);
}
/* =============================================== */
/*         Follow / Unfollow người dùng khác       */
/* =============================================== */
function follow(button) {
	var targetUserID = -1;
	if($(button).closest('.user').length) {
		targetUserID = parseInt($(button).closest('.user').find('.userID').attr('value'));
	}
	if($(button).closest('.bookmark').length) {
		targetUserID = parseInt($(button).closest('.bookmark').find('.userID').attr('value'))
	}
	$.ajax({
		type : 'POST',
		url : '/TagRecommend/network/follow',
		data : {
			'targetUserID' : targetUserID
		}, success : function(data) {
			if($('.network').hasClass('show') || $('.discover').hasClass('show')) {
				window.location.reload(true);	
			}
			if($('.search').hasClass('show')) {
				if(data.result === true) {
					$(button).removeClass('btn-warning').addClass('btn-success');
					$(button).html('<i class="fa fa-user-times"></i> Unfollow');
				} else {
					$(button).removeClass('btn-success').addClass('btn-warning');
					$(button).html('<i class="fa fa-user-plus"></i> Follow');
				}
			}
		}, error: function(xhr, textStatus, error) {
		      console.log(xhr.statusText);
		      console.log(textStatus);
		      console.log(error);
		      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate!</h4>')
		}
	});

}
/* =============================================== */
/*                   Xử lí Feedback                */
/* =============================================== */
function processFeedback() {
	if(!$('#feedback-area').val()) {
		bootbox.alert({
			size: 'small',
			message : '<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i> Feedback cannot be blank</h4>'
		});
	} else {
		var feedback = $('#feedback-area').val() ;
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/dashboard/feedback',
			data : {
				'feedback' : feedback,
			}, success : function(data) {
				if(data.status == "SUCCESS") {
					bootbox.alert({
						message: '<h4 class="text-center text-info"><i class="fa fa-check-square-o"></i>' + data.result + '</h4>',
						callback : function() {
							$('#feedback-area').val('') ;
						}
					});
				} else {
					bootbox.alert('<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i>' + data.result + '</h4>');
				}
			}, error: function(xhr, textStatus, error) {
			      console.log(xhr.statusText);
			      console.log(textStatus);
			      console.log(error);
			      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
			}
		});
	}
}
/* =============================================== */
/*                  Load Sortby Bar                */
/* =============================================== */
function loadSortByBar(element, sortBy) {
	$(element).html('<div class="row sortby"><div class="col-lg-2"><h4 class="text-center">Top Priority: </h4></div><div class="col-lg-10">' +
		'<div><ul class="nav  nav-justified">' +
		'<li class="sort-method"><a href="#" id="ar">Average Rating</a></li>' +
		'<li class="sort-method"><a href="#" id="st">Same Tags</a></li>' +
		'<li class="sort-method"><a href="#" id="vt">View Times</a></li>' +
		'<li class="sort-method"><a href="#" id="ct">Copy Times</a></li>' +
		'<li class="sort-method active"><a href="#" id="pt">Posted Time</a></li>' +
		'</ul></div></div></div>');
	switch(sortBy) {
	case 1: $('#ar').html('<i class="fa fa-check-circle"></i> Average Rating'); break;
	case 2: $('#st').html('<i class="fa fa-check-circle"></i> Same Tags'); break;
	case 3: $('#vt').html('<i class="fa fa-check-circle"></i> View Times'); break;
	case 4: $('#ct').html('<i class="fa fa-check-circle"></i> Copy Times'); break;
	case 5: $('#pt').html('<i class="fa fa-check-circle"></i> Posted Time'); break;
	}
}
/* =============================================== */
/*          Process Change Password Form           */
/* =============================================== */
function processChangePasswordForm() {
	var isReady = true;
	var message = null;
	if(!$('#old-password').val()) {
		message = '<p class="text-danger error-message"><i class="fa fa-exclamation-triangle"></i> Old Password can not be blank</p>';
		showError($('#old-password'), message);
		setTimeout(resetForm, 2000, $('#old-password')); 
		isReady = false;
	}
	if(!$('#new-password').val()) {
		message = '<p class="text-danger error-message"><i class="fa fa-exclamation-triangle"></i> New Password can not be blank</p>';
		showError($('#new-password'), message);
		setTimeout(resetForm, 2000, $('#new-password'));
		isReady = false;
	}
	if(!$('#new-password-confirm').val()) {
		message = '<p class="text-danger error-message"><i class="fa fa-exclamation-triangle"></i> Confirm new Password can not be blank</p>';
		showError($('#new-password-confirm'), message);
		setTimeout(resetForm, 2000, $('#new-password-confirm'));
		isReady = false;
	} else if ($('#new-password-confirm').val() != $('#new-password').val()) {
		message = '<p class="text-danger error-message"><i class="fa fa-exclamation-triangle"></i> Confirm and new Password are not match</p>';
		showError($('#new-password'), message);
		setTimeout(resetForm, 2000, $('#new-password-confirm'));
		isReady = false;
	}
	if(isReady === true) {
		var oldPassword = $('#old-password').val();
		var newPassword = $('#new-password').val();
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/settings/changePassword',
			data : {
				'oldPassword' : oldPassword,
				'newPassword' : newPassword
			}, success : function(data) {
				$('#change-password-form').find('button').html('Change Password');
				if(data.status == "SUCCESS") {
					bootbox.alert({
						message: '<h4 class="text-center text-info"><i class="fa fa-check-square-o"></i> ' + data.result + '</h4>',
						callback : function() {
							$('#old-password').val('') ;
							$('#new-password').val('') ;
							$('#new-password-confirm').val('') ;
						}
					});
				} else {
					bootbox.alert('<h4 class="text-center text-warning"><i class="fa fa-exclamation-triangle"></i> ' + data.result + '</h4>');
				}
			}, error: function(xhr, textStatus, error) {
			      console.log(xhr.statusText);
			      console.log(textStatus);
			      console.log(error);
			      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
			}
		});
	} else {
		$('#change-password-form').find('button').html('Change Password');
	}
}
function showError(element, message) {
	$(element).closest('.form-group').addClass('has-error').addClass('has-feedback');
	$(element).after('<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
	$(element).after(message);
}

function resetForm(element) {
	$(element).closest('.form-group').removeClass('has-error').removeClass('has-feedback');
	$(element).closest('.form-group').find('span').remove();
	$(element).closest('.form-group').find('p').remove();
}

/* =============================================== */
/*                  Crypto Function                */
/* =============================================== */
function crypto() {
//	var hashObj = new jsSHA("mySuperPassword", "ASCII");
//	var password = hashObj.getHash("SHA-512", "HEX");
//	$.jCryption.authenticate(password, "encrypt?generateKeyPair=true", "encrypt?handshake=true",
//			function(AESKey) {
//				$("#text,#encrypt,#decrypt,#serverChallenge").attr("disabled",false);
//				$("#status").html('<span style="font-size: 16px;">Let\'s Rock!</span>');
//			},
//			function() {
//				// Authentication failed
//			}
//	);
}