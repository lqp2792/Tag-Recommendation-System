<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/font-awesome.min.css" rel="stylesheet">
<link href="css/prettyPhoto.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<!--[if lt IE 9]>
        <script src="js/html5shiv.js"></script>
        <script src="js/respond.min.js"></script>
        <![endif]-->
</head>
<!--/head-->
<body>
	<c:if test="${validated eq true }"> phu
	<script type="text/javascript">
		$(document).ready(function() {
			$('#register-a').click();
		});
	</script>
	</c:if>
	<header id="header" role="banner">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="container">
			<div id="navbar" class="navbar navbar-default ">
				<div class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li><a href="#login" data-toggle="modal"
							data-target="#loginModal">Log In</a></li>
						<li><a href="#register" id="register-a" data-toggle="modal"
							data-target="#registerModal">Register</a></li>
					</ul>
				</div>
			</div>
		</div>
	</header>
	<!--/#header-->
	<section id="main-slider" class="carousel">
		<div class="carousel-inner">
			<div class="item active">
				<div class="container">
					<div class="carousel-content">
						<h1>Social Bookmarking Service</h1>

						<p class="lead">
							A centralized online service which enables users to add,
							annotate, edit <br />and share bookmarks of web documents
						</p>
					</div>
				</div>
			</div>
			<!--/.item-->
			<div class="item">
				<div class="container">
					<div class="carousel-content">
						<h1>Tag Recommendation</h1>

						<p class="lead">
							The task of predicting folksonomy tags for a given user and item,
							based on past user behavior <br />and possibly other information
						</p>
					</div>
				</div>
			</div>
			<!--/.item-->
		</div>
		<!--/.carousel-inner-->
		<a class="prev" href="#main-slider" data-slide="prev"><i
			class="icon-angle-left"></i></a> <a class="next" href="#main-slider"
			data-slide="next"><i class="icon-angle-right"></i></a>
	</section>

	<!-- Login Modal -->
	<div class="modal fade" id="loginModal" tabindex="-1" role="dialog"
		aria-labelledby="loginModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="loginModalLabel">Log
						In</h4>
				</div>
				<form:form action="login" method="post" role="form"
					modelAttribute="login" id="loginForm">
					<div class="modal-body">
						<div class="form-group">
							<form:label for="login-email" class="control-label"
								path="loginEmail">Email:</form:label>
							<form:input class="form-control input-sm" id="login-email"
								placeholder="Email" path="loginEmail" />
							<form:errors path="loginEmail" class="text-danger" />
						</div>
						<div class="form-group">
							<form:label for="login-password" class="control-label"
								path="loginPassword">Password:</form:label>
							<form:password class="form-control input-sm" id="login-password"
								placeholder="Your Password" path="loginPassword" />
							<form:errors path="loginPassword" class="text-danger" />
						</div>
					</div>
					<div class="modal-footer">
						<input type="submit" value="Login" class="btn btn-info btn-block">
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<!-- Register Modal -->
	<div class="modal fade" id="registerModal" tabindex="-1" role="dialog"
		aria-labelledby="registerModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="registerModalLabel">Register</h4>
				</div>
				<!-- Start Register Form -->
				<form:form action="register" modelAttribute="user" method="post"
					role="form" id="registerForm">
					<div class="modal-body">
						<div class="row">
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<form:label for="first-name" class="control-label"
										path="firstName"> First Name: 
									</form:label>
									<form:input class="form-control input-sm" path="firstName"
										id="first-name" placeholder="First Name" />
									<form:errors path="firstName" class="text-danger" />
								</div>
							</div>
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">

									<form:label for="last-name" class="control-label"
										path="lastName">Last Name: 
									</form:label>
									<form:input class="form-control input-sm" path="lastName"
										id="last-name" placeholder="Last Name" />
									<form:errors path="lastName" class="text-danger" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<form:label for="email-register" class="control-label"
								path="email">Email:
							</form:label>
							<form:input class="form-control input-sm" path="email"
								id="email-register" placeholder="Your Email" />
							<form:errors class="text-danger" path="email" />
						</div>
						<div class="form-group">
							<form:label for="password" class="control-label" path="password">Password:
							</form:label>
							<form:password class="form-control input-sm" path="password"
								id="password" placeholder="Your Password" />
							<form:errors class="text-danger" path="password" />
						</div>
					</div>
					<div class="modal-footer">
						<input type="submit" value="Register"
							class="btn btn-info btn-block">
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<!-- Javascript link -->
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/jquery.isotope.min.js"></script>
	<script src="js/jquery.prettyPhoto.js"></script>
	<script src="js/main.js"></script>
</body>
</html>