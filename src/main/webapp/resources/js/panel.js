/**
 * 
 */
var title = null;
var url = null;
var networkOffset = 0;
var availableTags = [];
/* =============================================== */
/* === Thay đổi page content, page header == */
/* =============================================== */
$(document).ready(function() {
	if (document.URL.indexOf('network') > -1) {
		$('.bookmarks').removeClass('show').addClass('hidden');
		$('.network').removeClass('hidden').addClass('show');
		$.ajax({
			type : 'GET',
			url : '/TagRecommend/network/getBookmarks',
			data : {
				offset : networkOffset
			},
			success : function(data) {
				console.log(data);
				$('.page-content').find('.network').html('');
				for (i = 0; i < data.result.length; i++) {
					var bookmark = data.result[i];
					var inner = '<div class="bookmark">';
					inner += '<div class="row"><div class="col-lg-12">';
					inner += '<h3>' + bookmark.title + '</h3>';
					inner += '</div></div>';
					inner += '<div class="row"><div class="col-lg-9">';
					inner += '<a class="text-info" href="' + bookmark.url + '">' + bookmark.url + '</a>';
					inner += '</div><div class="col-lg-3">';
					inner += '<p>Last Updated: ' + bookmark.date + '</p>';
					inner += '</div></div>';
					inner += '<div class="row"><div class="col-lg-9">';
					inner += '<div class="inline-div">';
					inner += '<i class="fa fa-tags"></i> Tags: ';
					for (j = 0; j < bookmark.tags.length; j++) {
						var tag = bookmark.tags[j];
						var id = 'menu' + bookmark.bookmarkID + '-' + j;
						inner += '<div class="dropdown inline-div">';
						inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
								+ id + '">#' + tag + '</code>';
						inner += '<ul class="dropdown-menu" role="menu" aria-labelledby="' + id + '">';
						inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Filter by #' + tag + '</a></li>';
						inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"> Find #' + tag + ' on System</a></li>';
						inner += '</ul></div>';
					}
					inner += '</div>';
					inner += '</div><div class="col-lg-3"><div class="btn-toolbar">';
					inner += '<button class="btn btn-sm btn-success" data-toggle="modal" data-target="#nw-add-tag-modal"><i class="fa fa-pencil-square-o"></i> Add Tag</button>';
					inner += '<button class="btn btn-sm btn-success"><i class="fa fa-files-o"></i> Copy (1)</button>';
					inner += '<input type="hidden" name="bookmarkID" value="1">';
					inner += '</div></div></div>';
					inner += '<div class="row"><div class="col-lg-12">';
					inner += '<p>Posted by: ' + bookmark.firstName + ' ' + bookmark.lastName + '</p>';
					inner += '</div></div></div>';
					$('.page-content').find('.network').append(inner);
				}
				$('.page-content').find('.network').append(
								'<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
				networkOffset += 5;
				infiniteScroll();
			}
		});
		/* =============================================== */
		/* Autocomplete text khi add tag */
		/* =============================================== */
		$.ajax({
			type : 'GET',
			url : '/TagRecommend/network/getAlphabet',
			success : function(data) {
				console.log(data);
				availableTags = data.result;
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
						this.value = terms.join(",");
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
			}
		});
	}
	function split(val) {
		return val.split(/,\s*/);
	}
	function extractLast(term) {
		return split(term).pop();
	}
	/* Kiểm tra nếu vào discover */
	if(document.URL.indexOf('discover') > -1) {
		if($('.bookmarks').hasClass('show')) {
			$('.bookmarks').removeClass('show').addClass('hidden');	
		}
		if($('.network').hasClass('show')) {
			$('.network').removeClass('show').addClass('hidden');
		}
		$('.discover').removeClass('hidden').addClass('show');
	}
});
/* =============================================== */
$(document).ready(function() {
	$('#add-bookmark-form').submit(function(e) {
		e.preventDefault();
		$("#add-bookmark-submit").removeClass('fa-plus').addClass('fa-spinner').addClass('fa-spin');
		$.ajax({
			type : "POST",
			data : $('#add-bookmark-form').serialize(),
			url : "/TagRecommend/dashboard/checkBookmark",
			async : false,
			dataType : 'json',
			success : function(data) {
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
			},
		});
	});
});
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
/* Click bookmark tag hiện dropdown menu */
$(document).on("click", ".bookmark-tag", function(e) {
	clickBookmarkTag(this);
});
/* =============================================== */
/* === Click trên các tag hiện dropmenu == */
/* =============================================== */
function clickBookmarkTag(elm) {
	console.log('clicked');
	if (!$(elm).parent().find('.dropdown-menu').length) {
		$(elm).addClass('dropdown-toogle').attr('id', 'menu1').attr(
				'data-toggle', 'dropdown');
		$(elm)
				.after(
						'<ul class="dropdown-menu" role="menu" aria-labelledby="menu1">'
								+ '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Filter by '
								+ $(elm).text()
								+ '</a></li>'
								+ '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"> Find '
								+ $(elm).text() + ' on System</a></li></ul>');
	}
}
/* =============================================== */
/* === Lấy các tag được gợi ý khi post bookmark == */
/* =============================================== */
function getRecommendTag() {
	$('.waiting-div')
			.html(
					'<div id="waiting-message-tag"><i class="fa fa-spinner fa-spin"></i> Getting Recommend Tag</div>');
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
/* Click discover menu */
/* =============================================== */
$(document).ready(function(e){
	if(document.URL.indexOf('discover') > -1) {
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/discover/subscription',
			success : function(data) {
				console.log(data);
				var text = 'Your most used tags: ';
				var tags = [];
				for (i = 0; i < data.result[0].length; i++) {
					tags[i] = data.result[0][i].tag;
					text += '<span>(' + data.result[0][i].weight + ')<code>#' + data.result[0][i].tag + '</code></span>';
				}
				$('#discover-most-used-tags').html(text);
				var subscription = null;
				if (data.result[1] == "EMPTY") {
					subscription = '<h4 class="text-danger">You did not set tag subcription</h4>';
					$('#discover-subscription').html(subscription);
					$('.page-content').find('.discover').append('<p class="text-center text-info">System will use'
											+ 'top 5 most used tags for subscription as default!</p>');
					getDefaultSubscription(tags);
				} else {
					// / nếu có dùng tag subscription
				}
			}
		});
	}
});
	
/* =============================================== */
/* Discover các Bookmark theo default subscription */
/* =============================================== */
function getDefaultSubscription(tags) {
	if (tags.length > 0) {
		$.ajax({
			type : 'POST',
			data : {
				subscriptionTags : tags
			},
			traditional : true,
			url : '/TagRecommend/discover/defaultDiscover',
			success : function(data) {
				console.log(data);
				$('.page-content').find('.discover').html('');
				for (i = 0; i < data.result.length; i++) {
					var bookmark = data.result[i];
					var inner = '<div class="bookmark">';
					inner += '<h3>' + bookmark.title + '</h3>';
					inner += '<a class="text-info" href="'
							+ bookmark.url + '">' + bookmark.url
							+ '</a>';
					inner += '<p>';
					inner += '<i class="fa fa-tags"></i> Tags: ';
					for (j = 0; j < bookmark.tags.length; j++) {
						var tag = bookmark.tags[j];
						inner += '<span class="dropdown"><code class="bookmark-tag">'
								+ '#' + tag + '</code></span>';
					}
					inner += '</p></div>';
					$('.page-content').find('.discover').append(inner);
				}
			}
		});
	} else {
		$('.page-content').find('.discover').html('You did not post any Bookmark!');
	}
}
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
function infiniteScroll() {
	var controller = new ScrollMagic.Controller();
	var scene = new ScrollMagic.Scene({
		triggerElement : ".dynamicContent #loader",
		triggerHook : "onEnter"
	}).addTo(controller).on("enter", function(e) {
		if (!$("#loader").hasClass("active")) {
			$("#loader").addClass("active");
			if (console) {
				console.log("loading new items");
			}
			setTimeout(addElements, 3000);
		}
	});

	function addElements() {
		console.log('get new element');
		$
				.ajax({
					type : 'GET',
					url : '/TagRecommend/network/getBookmarks',
					data : {
						offset : networkOffset
					},
					success : function(data) {
						for (i = 0; i < data.result.length; i++) {
							var bookmark = data.result[i];
							var inner = '<div class="bookmark">';
							inner += '<div class="row"><div class="col-lg-12">';

							inner += '<h3>' + bookmark.title + '</h3>';
							inner += '</div></div>';
							inner += '<div class="row"><div class="col-lg-9">';
							inner += '<a class="text-info" href="'
									+ bookmark.url + '">' + bookmark.url
									+ '</a>';
							inner += '</div><div class="col-lg-3">';
							inner += '<p>Last Updated: ' + bookmark.date
									+ '</p>';
							inner += '</div></div>';
							inner += '<div class="row"><div class="col-lg-9">';
							inner += '<div class="inline-div">';
							inner += '<i class="fa fa-tags"></i> Tags: ';
							for (j = 0; j < bookmark.tags.length; j++) {
								var tag = bookmark.tags[j];
								var id = 'menu' + bookmark.bookmarkID + '-' + j;
								inner += '<div class="dropdown inline-div">';
								inner += '<code class="bookmark-tag dropdown-toogle" data-toggle="dropdown" id="'
										+ id + '">#' + tag + '</code>';
								inner += '<ul class="dropdown-menu" role="menu" aria-labelledby="'
										+ id + '">';
								inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Filter by #'
										+ tag + '</a></li>';
								inner += '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"> Find #'
										+ tag + ' on System</a></li>';
								inner += '</ul></div>';
							}
							inner += '</div>';
							inner += '</div><div class="col-lg-3"><div class="btn-toolbar">';
							inner += '<button class="btn btn-sm btn-success" data-toggle="modal" data-target="#nw-add-tag-modal"><i class="fa fa-pencil-square-o"></i> Add Tag</button>';
							inner += '<button class="btn btn-sm btn-success"><i class="fa fa-files-o"></i> Copy (1)</button>';
							inner += '<input type="hidden" name="bookmarkID" value="1">';
							inner += '</div></div></div>';
							inner += '<div class="row"><div class="col-lg-12">';
							inner += '<p>Posted by: ' + bookmark.firstName
									+ ' ' + bookmark.lastName + '</p>';
							inner += '</div></div></div>';

							$('.page-content').find('.network').append(inner);
							$('#loader').remove();

						}
						if (data.result.length == 5) {
							$('.page-content').find('.network').append('<div id="loader" style="text-align: center;"><i class="fa fa-spinner fa-spin"></i> LOADING...</div>');
							networkOffset += 5;
						} else {
							$('.page-content').find('.network').append('<div class="center-div"><h4><a href="#" id="backToTop"><i class="fa fa-reply"></i> THERE IS NO MORE BOOKMARKS</a></h4></div>');
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
/* Network - Thêm tag vào bookmark của người dùng khác */
/* =============================================== */
$(document).ready(function() {
	$('#nw-add-tag-form').submit(function(e) {
		e.preventDefault();
		alert('d');
		if(!$('#nw-tag').val()) {
			$(this).after('<div style="margin-top: 20px;" class="alert alert-danger" role="alert"><i class="fa fa-times"></i> Tag can not be blank</div>');
		} else {
			var tags = $('#nw-tag').val().split(',');
			var bookmark = $(this).closest('.bookmark');
			alert(bookmark.length);
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
					console.log(data);
				}
			});
		}
	});
});