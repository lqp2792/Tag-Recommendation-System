<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/metisMenu.min.css" rel="stylesheet">
<link href="css/sb-admin-2.css" rel="stylesheet">
<link href="css/panel.css" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet" type="text/css">
<script src="js/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/metisMenu.min.js"></script>
<script src="js/sb-admin-2.js"></script>
<script src="js/bootbox.js"></script>
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="js/html5shiv.js"></script>
        <script src="js/respond.min.js"></script>
        <![endif]-->
<script type="text/javascript">
	var title = null;
	var url = null;
	$(document)
			.ready(
					function() {
						$('#add-bookmark-form')
								.submit(
										function(e) {
											e.preventDefault();
											$("#add-bookmark-submit")
													.removeClass('fa-plus')
													.addClass('fa-spinner')
													.addClass('fa-spin');
											$
													.ajax({
														type : "POST",
														data : $(
																'#add-bookmark-form')
																.serialize(),
														url : "/TagRecommend/dashboard/add",
														async : false,
														dataType : 'json',
														success : function(data) {
															if (data.status == 'SUCCESS') {
																title = data.result.title;
																url = data.result.url;
																$(
																		'#bookmark-url')
																		.html(
																				data.result.url);
																$(
																		'#bookmark-title')
																		.html(
																				data.result.title);
																$(
																		'#add-bookmark-modal')
																		.modal(
																				'hide');
																$(
																		'#add-tag-modal')
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
</script>

<script type="text/javascript">
	$(document).ready(
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
				$('[data-toggle="tooltip"]').tooltip();
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
				"url" : url
			},
			success : function(data) {
				console.log(data);
				for (i = 0; i < data.result.length; i++) {
					$('#recommend-tags-div').append(
							'<strong>Topic ' + data.result[i].topicID + " - "
									+ data.result[i].topicProbality.toFixed(2)
									+ "</strong><br /><p>");
					var tags = data.result[i].recommendTags;
					for (j = 0; j < tags.length; j++) {
						$('#recommend-tags-div').append(
								'<code class="recommend-tag" style="cursor : copy"> #'
										+ tags[j].content + ' </code>');
					}
					$('#recommend-tags-div').append('</p><br />');
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
</script>
</head>
<body>
	<div id="wrapper">
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
					<span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.html">SB Admin v2.0</a>
			</div>
			<!-- /.navbar-header -->
			<ul class="nav navbar-top-links navbar-right">
				<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown"
					href="#"> <i class="fa fa-envelope fa-fw"></i> <i
						class="fa fa-caret-down"></i>
				</a>
					<ul class="dropdown-menu dropdown-messages">
						<li><a href="#">
								<div>
									<strong>John Smith</strong> <span class="pull-right text-muted">
										<em>Yesterday</em>
									</span>
								</div>
								<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
									Pellentesque eleifend...</div>
						</a></li>
						<li class="divider"></li>
						<li><a href="#">
								<div>
									<strong>John Smith</strong> <span class="pull-right text-muted">
										<em>Yesterday</em>
									</span>
								</div>
								<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
									Pellentesque eleifend...</div>
						</a></li>
						<li class="divider"></li>
						<li><a href="#">
								<div>
									<strong>John Smith</strong> <span class="pull-right text-muted">
										<em>Yesterday</em>
									</span>
								</div>
								<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
									Pellentesque eleifend...</div>
						</a></li>
						<li class="divider"></li>
						<li><a class="text-center" href="#"> <strong>Read All
									Messages</strong> <i class="fa fa-angle-right"></i>
						</a></li>
					</ul> <!-- /.dropdown-messages --></li>
			</ul>
			<!-- /.navbar-top-links -->
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<li class="sidebar-search">
							<div class="input-group custom-search-form">
								<input type="text" class="form-control" placeholder="Search...">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button">
										<i class="fa fa-search"></i>
									</button>
								</span>
							</div> <!-- /input-group -->
						</li>
						<li><a href="#"><i class="fa fa-files-o fa-fw"></i> My Links</a></li>
						<li><a href="#"><i class="fa fa-users fa-fw"></i> Network</a></li>
						<li><a href="#"><i class="fa fa-globe fa-fw"></i> Discover</a></li>
						<li><a href="#"><i class="fa fa-star-o fa-fw"></i> Trending</a></li>
						<li><a href="#" id="add-bookmark-menu" data-toggle="modal"
							data-target="#add-bookmark-modal"><i class="fa fa-plus-square fa-fw"></i>
								Add Link</a></li>
						<li><a href="#"><i class="fa fa-cog fa-fw"></i> Setting</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">
							<i class="fa fa-spinner fa-spin fa-3x"></i>
						</h1>
					</div>
				</div>
			</div>
		</div>
		<!-- Add Link Modal -->
		<div class="modal fade" id="add-bookmark-modal" tabindex="-1" role="dialog"
			aria-labelledby="addBookmarkModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center" id="addBookmarkModalLabel">
							<i class="fa fa-pencil"></i> Share New Link
						</h4>
					</div>
					<form:form method="post" role="form" modelAttribute="newBookmark"
						id="add-bookmark-form" action="dashboard/add">
						<div class="modal-body">
							<div class="form-group">
								<form:label for="new-bookmark-textarea"
									class="control-label text-primary" path="url" id="new-bookmark-label">New Bookmark:</form:label>
								<form:textarea class="form-control" rows="5" id="new-bookmark-textarea"
									placeholder="Url Link" path="url" style="resize : none" />
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus" id="add-bookmark-submit"></i> Add Bookmark
							</button>
						</div>
					</form:form>
				</div>
			</div>
		</div>
		<!-- Add Tag Modal -->
		<div class="modal fade" id="add-tag-modal" tabindex="-1" role="dialog"
			aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center">
							<i class="fa fa-tag"></i> Add Tag
						</h4>
					</div>
					<form method="post" role="form" id="add-tag-form" action="dashboard/add">
						<div class="modal-body">
							<div class="form-group">
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">Url :</p></strong>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-url"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">Title :</p></strong>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-title"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">
												<i class="fa fa-tag"></i>Tags :
											</p></strong>
									</div>
									<div class="col-xs-9">
										<textarea class="form-control" id="bookmark-tags" rows="3"
											placeholder="Tags" style="resize: none" data-toggle="tooltip"
											data-placement="right"
											data-original-title="Tag are important part of organizing on Social Bookmarking Service. Using tags that are smart and simple benifits both you and community"></textarea>
										<p class="text-info">
											Recommend Tags <i class="fa fa-question" data-toggle="tooltip"
												data-placement="right"
												data-original-title="System use Topic Modeling to recommend tags to user"></i>
										</p>
										<div class="waiting-div"></div>
										<div id="recommend-tags-div"></div>
										<%-- 		<c:if test="${not empty topics}">
											<c:forEach var="topic" items="${topics }">
												<p>${topic.topicID}${topic.topicProbality}</p>
												<c:forEach var="tag" items="${topic.recommendTags}">
													<p>${tag.content}</p>
												</c:forEach>
											</c:forEach>
										</c:if> --%>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="form-group">
										<div class="col-xs-3">
											<strong><p class="text-info">
													<i class="fa fa-comment"></i> Comment :
												</p></strong>
										</div>
										<div class="col-xs-9">
											<textarea class="form-control" id="bookmark-comment" rows="4"
												placeholder="Your Comment" style="resize: none"
												data-toggle="tooltip" data-placement="right"
												data-original-title="What are you thinking about this Bookmark?"></textarea>
										</div>
									</div>

								</div>
								<%-- <form:label for="new-bookmark-textarea"
									class="control-label text-primary" path="url" id="new-bookmark-label">New Bookmark:</form:label>
								<form:textarea class="form-control" rows="5" id="new-bookmark-textarea"
									placeholder="Url Link" path="url" style="resize : none" /> --%>
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus"></i> Add Tags
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>