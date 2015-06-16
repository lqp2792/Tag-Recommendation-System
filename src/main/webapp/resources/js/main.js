var password = null
$(document).on('click', '#register-menu', function(e) {
	$('#login-modal').modal('hide');
	$('#first-name').val('');
	$('#last-name').val('');
	$('#email-register').val('');
	$('#password').val('');
});
$(document).on('click', '#login-menu', function(e) {
	$('#register-modal').modal('hide');
});
$(document).ready(function(e) {
	var hashObj = new jsSHA("lequangphu", "ASCII");
	password = hashObj.getHash("SHA-512", "HEX");
 
	$.jCryption.authenticate(password, "encrypt?generateKeyPair=true", "encrypt?handshake=true",
			function(AESKey) {
				$("#login-button, #register-button").attr("disabled",false);
			},
			function() {
				bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Encrypt Authentication Failed!</h4>');
			}
	);
	
	$('.tlt-h').textillate({
		minDisplayTime: 7000,  
	    out :{  delay: 3, effect: 'lightSpeedOut'},
	    loop: true
	});
	$('.tlt-p').textillate({
	    minDisplayTime: 1000, 
	    out :{  delay: 3, effect: 'lightSpeedOut'},
	    loop: true
	});
	/* center modal */
	function centerModals() {
		$('.modal').each(function(i) {
			var $clone = $(this).clone().css('display', 'block').appendTo('body');
			var top = Math.round(($clone.height() - $clone.find('.modal-content').height()) / 2);
			top = top > 0 ? top : 0;
			$clone.remove();
			$(this).find('.modal-content').css("margin-top", top);
		});
	}
	$('.modal').on('show.bs.modal', centerModals);
	$(window).on('resize', centerModals);
	$('#login-form').submit(function(e) {
		e.preventDefault();
		$(this).find('button').html('<i class="fa fa-spinner fa-spin"></i> Processing');
		submitLoginForm();
	});
	$('#register-form').submit(function(e) {
		e.preventDefault();
		$(this).find('button').html('<i class="fa fa-spinner fa-spin"></i> Processing');
		submitRegisterForm();
	});
});
function submitLoginForm() {
	var isReady = true;
	var message = null;
	if(!$('#login-email').val()) {
		message = '<p class="text-danger error-message">Email can not be blank</p>';
		showError($('#login-email'), message);
		setTimeout(resetForm, 2000, $('#login-email')); 
		isReady = false;
	}  else if (!validateEmail($('#login-email').val())) {
		message = '<p class="text-danger error-message">Email is not valid</p>';
		showError($('#login-email'), message);
		setTimeout(resetForm, 2000, $('#login-email'));
		isReady = false;
	}
	if(!$('#login-password').val()) {
		message = '<p class="text-danger error-message">Password can not be blank</p>';
		showError($('#login-password'), message);
		setTimeout(resetForm, 2000, $('#login-password'));
		isReady = false;
	}
	if(isReady === true) {
		var email = $('#login-email').val();
		var password = $('#login-password').val();
		$.ajax({
			type : 'POST',
			url : "/TagRecommend/login",
			data : {
				'encryptedEmail' : encryptText(email),
				'encryptedPassword' : encryptText(password)
			}, success : function(data) {
				$('#login-form').find('button').html('Login');
				if (data.status == "SUCCESS") {
					window.location.href = "/TagRecommend/dashboard";
				} else {
					bootbox.alert('<h3 class="text-center text-success">' + data.result + '</h3>');
				}
			}, error: function(xhr, textStatus, error) {
			      console.log(xhr.statusText);
			      console.log(textStatus);
			      console.log(error);
			      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
			}
		});
	} else {
		$('#login-form').find('button').html('Login');
	}
}

function submitRegisterForm() {
	var isReady = true;
	var message = null;
	if(!$('#first-name').val()) {
		message = '<p class="text-danger error-message">First Name can not be blank</p>';
		showError($('#first-name'), message);
		setTimeout(resetForm, 2000, $('#first-name')); 
		isReady = false;
	} 
	if(!$('#last-name').val()) {
		message = '<p class="text-danger error-message">Last Name can not be blank</p>';
		showError($('#last-name'), message);
		setTimeout(resetForm, 2000, $('#last-name')); 
		isReady = false;
	} 
	if(!$('#email-register').val()) {
		message = '<p class="text-danger error-message">Email can not be blank</p>';
		showError($('#email-register'), message);
		setTimeout(resetForm, 2000, $('#email-register'));
		isReady = false;
	} else if (!validateEmail($('#email-register').val())) {
		message = '<p class="text-danger error-message">Email is not valid</p>';
		showError($('#email-register'), message);
		setTimeout(resetForm, 2000, $('#email-register'));
		isReady = false;
	}
	if(!$('#password').val()) {
		message = '<p class="text-danger error-message">Password can not be blank</p>';
		showError($('#password'), message);
		setTimeout(resetForm, 2000, $('#password'));
		isReady = false;
	}

	if(isReady === true) {
		var email = $('#email-register').val();
		var password = $('#password').val();
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/register',
			data : {
				'firstName' : $('#first-name').val(),
				'lastName' : $('#last-name').val(),
				'encryptedEmail' : encryptText(email),
				'encryptedPassword' : encryptText(password)
			}, success : function(data) {
				$('#register-form').find('button').html('Register');
				$('#register-modal').modal('hide');
				if(data.status == "SUCCESS") {
					bootbox.alert('<h3 class="text-center text-success">' + data.result + '</h3>', function(e) {
						$.ajax({
							type : 'POST',
							url : "/TagRecommend/login",
							data : {
								'encryptedEmail' : encryptText(email),
								'encryptedPassword' : encryptText(password)
							}, success : function(data) {
								if (data.status == "SUCCESS") {
									window.location.href = "/TagRecommend/dashboard";
								} else {
									bootbox.alert('<h3 class="text-center text-success">' + data.result + '</h3>');
								}
							}, error: function(xhr, textStatus, error) {
							      console.log(xhr.statusText);
							      console.log(textStatus);
							      console.log(error);
							      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
							}
						});
					});
				} else {
					bootbox.alert('<h3 class="text-center text-warning">' + data.result + '</h3>');
				}
			}, error: function(xhr, textStatus, error) {
			      console.log(xhr.statusText);
			      console.log(textStatus);
			      console.log(error);
			      bootbox.alert('<h4 class="text-center text-danger"><i class="fa fa-exclamation-triangle"></i> Something has happend while communicate with Server!</h4>')
			}
		});
	} else {
		$('#register-form').find('button').html('Register');
	}
}

function validateEmail(email) {
	var filter = /^[\w\-\.\+]+\@[a-zA-Z0-9\.\-]+\.[a-zA-z0-9]{2,4}$/;
	if (filter.test(email)) {
		return true;
	} else {
		return false;
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

function encryptText(text) {
	return $.jCryption.encrypt(text, password);
}