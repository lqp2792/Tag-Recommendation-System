/**
 * 
 */
var title = null;
var url = null;
$(document)
		.ready(
				function() {
					$('#add-bookmark-form')
							.submit(
									function(e) {
										e.preventDefault();
										$("#add-bookmark-submit").removeClass(
												'fa-plus').addClass(
												'fa-spinner').addClass(
												'fa-spin');
										$
												.ajax({
													type : "POST",
													data : $(
															'#add-bookmark-form')
															.serialize(),
													url : "/TagRecommend/dashboard/checkBookmark",
													async : false,
													dataType : 'json',
													success : function(data) {
														if (data.status == 'SUCCESS') {
															title = data.result.title;
															url = data.result.url;
															$('#bookmark-url')
																	.html(
																			data.result.url);
															$('#bookmark-title')
																	.html(
																			data.result.title);
															$(
																	'#add-bookmark-modal')
																	.modal(
																			'hide');
															$('#add-tag-modal')
																	.modal(
																			'show');

														} else {
															var submitButton = $('#add-bookmark-submit');
															if (submitButton
																	.hasClass('fa-spin')) {
																submitButton
																		.removeClass(
																				'fa-spinner')
																		.removeClass(
																				'fa-spin')
																		.addClass(
																				'fa-plus');
															}
															var errorHtml = '<div id="error-message"><br/><i class="fa fa-times"> </i>'
																	+ '<span class="text-danger"> '
																	+ data.result
																	+ ' </span></div>';
															$(
																	"#new-bookmark-textarea")
																	.after(
																			errorHtml);
															var timeout = setTimeout(
																	function() {
																		$(
																				'#error-message')
																				.remove();
																	}, 3000);

														}
													},
												});
									});
				});
$(document)
		.ready(
				function() {
					$('#add-bookmark-modal').on(
							'hidden.bs.modal',
							function(e) {
								$(this).find('#new-bookmark-textarea').val('');
								var submitButton = $('#add-bookmark-submit');
								if (submitButton.hasClass('fa-spin')) {
									submitButton.removeClass('fa-spinner')
											.removeClass('fa-spin').addClass(
													'fa-plus');
								}
							});
					$('#add-bookmark-modal').on(
							'shown.bs.modal',
							function(e) {
								var submitButton = $('#add-bookmark-submit');
								if (submitButton.hasClass('fa-spin')) {
									submitButton.removeClass('fa-spinner')
											.removeClass('fa-spin').addClass(
													'fa-plus');
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
					// // on click bookmark tag
					$('.bookmark-tag')
							.click(
									function(e) {
										if (!$(this).parent().find(
												'.dropdown-menu').length) {
											$(this).addClass('dropdown-toogle')
													.attr('id', 'menu1').attr(
															'data-toggle',
															'dropdown');
											$(this)
													.after(
															'<ul class="dropdown-menu" role="menu" aria-labelledby="menu1">'
																	+ '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Filter by '
																	+ $(this)
																			.text()
																	+ '</a></li>'
																	+ '<li role="presentation"><a role="menuitem" tabindex="-1" href="#"> Find '
																	+ $(this)
																			.text()
																	+ ' on System</a></li></ul>');
										}

									});
					$('#discover-menu')
							.click(
									function() {
										var header = $('.page-header').html('');
										header
												.append('<h2> Please setting tag subscription: </h2>');
										header
												.append('<div class="tag-subscription"></div>');
										header
												.append('<div id="most-used-tags">Your most used tags: </div>');
										$('.page-content').html('');
										$
												.ajax({
													type : 'POST',
													url : '/TagRecommend/dashboard/subscription',
													success : function(data) {
														console.log(data);
														String
														text = 'Your most used tags: ';
														for (i = 0; i < data.result.length; i++) {
															text += '<span>('
																	+ data.result[i].weight
																	+ ')<code>#'
																	+ data.result[i].tag
																	+ '</code></span>';
														}
														$('#most-used-tags')
																.html(text);
													}
												});

									});
				});
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
							$('#bookmark-tags').val(
									content + ' ' + $(this).text().trim());
						}

					});
		},
		complete : function() {
			$('#waiting-message-tag').remove();
		}
	});
};

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