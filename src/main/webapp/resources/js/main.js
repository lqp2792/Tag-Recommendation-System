jQuery(function($) {
	$(function() {
		$('#main-slider.carousel').carousel({
			interval : 7000,
			pause : false
		});
	});
});

$(document)
		.ready(
				function(e) {
					$('#login-modal').on('shown.bs.modal', function(e) {
						$('#register-modal').modal('hide');
					});
					$('#register-modal').on('shown.bs.modal', function(e) {
						$('#login-modal').modal('hide');
					});
					/* center modal */
					function centerModals() {
						$('.modal').each(
								function(i) {
									var $clone = $(this).clone().css('display',
											'block').appendTo('body');
									var top = Math
											.round(($clone.height() - $clone
													.find('.modal-content')
													.height()) / 2);
									top = top > 0 ? top : 0;
									$clone.remove();
									$(this).find('.modal-content').css(
											"margin-top", top);
								});
					}
					$('.modal').on('show.bs.modal', centerModals);
					$(window).on('resize', centerModals);
					// Login ajax
					$('#login-form')
							.submit(
									function(e) {
										var submit = false;
										var email = $('#login-email').val();
										var password = $('#login-password')
												.val();
										e.preventDefault();
										if (!email || !password) {
											submit = false;
											if (!email) {
												$('#login-email').parent()
														.addClass('has-error');
												$('#login-email')
														.parent()
														.find('.error-message')
														.html(
																'<p class="text-danger error-message">'
																		+ '<i class="fa fa-times"></i> Email can not be blank!</p>')
											}

											if (!password) {
												$('#login-password').parent()
														.addClass('has-error');
												$('#login-password')
														.parent()
														.find('.error-message')
														.html(
																'<p class="text-danger error-message">'
																		+ '<i class="fa fa-times"></i> Password can not be blank!</p>')
											}

										} else {
											submit = true;
										}
										if (submit === true) {
											$
													.ajax({
														type : 'POST',
														url : "/TagRecommend/login",
														data : {
															'email' : email,
															'password' : password
														},
														success : function(data) {
															if (data.status == "SUCCESS") {
																window.location.href = "/TagRecommend/dashboard";
															} else {
																$(
																		'.modal-title')
																		.parent()
																		.find(
																				'.error-message')
																		.html(
																				'<p class="><br/><i class="fa fa-times"></i>'
																						+ data.result);
															}

														}
													});
										}
									});
					$('#register-modal').submit(function(e) {
					});
				});