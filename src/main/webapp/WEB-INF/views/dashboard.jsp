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
<link href="css/jquery-ui.css" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet" type="text/css">

</head>
<body>
	<div id="wrapper">
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="."><c:out
						value="${firstName} ${lastName}" /></a>
			</div>
			<!-- /.navbar-header -->
			<ul class="nav navbar-top-links navbar-right">
				<li class="active" id="log-out"><a href="#">Log out</a></li>
			</ul>
			<!-- /.navbar-top-links -->
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<li class="sidebar-search">
							<div class="input-group custom-search-form">
								<input type="text" class="form-control" placeholder="Search..."
									data-toggle="tooltip" data-placement="right" data-html="true"
									data-original-title="Search tips: <br/> - #tagname - Search a specific tag <br/>
									 - @username - Search a specific user <br /> - keyword - Search a specific keyword<br/>">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button">
										<i class="fa fa-search"></i>
									</button>
								</span>
							</div> <!-- /input-group -->
						</li>
						<li><a href="#" id="my-bookmarks-menu"><i
								class="fa fa-align-justify fa-fw"></i> My Bookmarks</a></li>
						<li><a href="network" id="network-menu"><i
								class="fa fa-users fa-fw"></i> Network</a></li>
						<li><a href="#" id="discover-menu"><i
								class="fa fa-globe fa-fw"></i> Discover</a></li>
						<li><a href="#" id="trending-menu"><i
								class="fa fa-star-o fa-fw"></i> Trending</a></li>
						<li><a href="#" id="add-bookmark-menu" data-toggle="modal"
							data-target="#add-bookmark-modal"><i
								class="fa fa-plus-square fa-fw"></i> Add Bookmark</a></li>
						<li><a href="#" id="setting-menu"><i
								class="fa fa-cog fa-fw"></i> Setting</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row page-header">
					<!-- =========================== -->
					<!-- Default page -->
					<!-- =========================== -->
					<div class="bookmarks show">
						<div class="col-lg-2">
							<img src="images/user.png" class="img-thumbnail"
								alt="User Default Avatar"
								style="min-height: 100px; height: 100px;">
						</div>
						<div class="col-lg-10">
							<h2>
								<c:out value="${firstName} ${lastName}" />
							</h2>
							<h4 class="text-info">0 Bookmark 0 Following 0 Follower</h4>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào discover -->
					<!-- =========================== -->
					<div class="discover hidden">
						<h2>Please setting tag subscription:</h2>
						<div id="discover-subscription"></div>
						<div id="discover-most-used-tags">Your most used tags:</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào Network -->
					<!-- =========================== -->
					<div class="network hidden">
						<div class="row text-center">
							<h3 class="text-info">
								Recommend Users <span><i style="cursor: pointer;"
									class="fa fa-question fa-border" data-toggle="tooltip"
									data-placement="right" data-html="true"
									data-original-title="5 Users will be recommended based on their interest (topic + tags): <br/> - Same topic count: how many same topic was used <br/>
									 - Same tag count - How many same tag was used <br />"></i></span>
							</h3>

						</div>
						<div class="row row-centered">
							<c:if test="${not empty recommendUsers}">
								<c:forEach var="recommendUser" items="${recommendUsers}">
									<div class="col-md-15 col-sm-3 col-centered">
										<div class="thumbnail clearfix">
											<img src="images/user.png" class="pull-left"
												alt="User Default Avatar"
												style="min-height: 75px; height: 75px; min-width: 75px; width: 75px; margin-right: 5px">
											<div>
												<input type="hidden" name="userID" class="userID"
													value="<c:out value="${recommendUser.userID}" />">
												<p class="text-center">
													<c:out
														value="${recommendUser.firstName}
													${recommendUser.lastName}" />
												</p>
												<c:set var="tooltip_tag" value="Top Used Tags (Used times):" />
												<c:set var="sharp" value="#" />
												<c:forEach var="t" items="${recommendUser.mostUsedTags}">
													<c:set var="tooltip_tag"
														value="${tooltip_tag} <br /><code>${sharp}${t.tag}</code> - (${t.weight})" />
												</c:forEach>
												<small> Top used tags: <i class="fa fa-info-circle"
													style="cursor: pointer;" data-toggle="tooltip"
													data-placement="right" data-html="true"
													data-original-title="<c:out value="${tooltip_tag}"/>"></i>
												</small><br />
												<c:set var="tooltip_topic" value="Top Used Topics:" />
												<c:forEach var="topic"
													items="${recommendUser.mostUsedTopics}">
													<c:set var="tooltip_topic"
														value="${tooltip_topic} <br />- <code>${topic}</code>" />
												</c:forEach>
												<small> Top used topics: <i
													class="fa fa-info-circle" style="cursor: pointer;"
													data-toggle="tooltip" data-placement="right"
													data-html="true"
													data-original-title="<c:out value="${tooltip_topic}"/>"></i>
												</small>
											</div>

										</div>
										<div class="text-center">
											<button class="btn btn-default btn-xs btn-success follow"
												type="button">
												<i class="fa fa-user-plus"></i> Follow
											</button>
										</div>
									</div>
								</c:forEach>
							</c:if>
							<c:if test="${empty recommendUsers}">
								<p class="text-warning">System can not recommend users.
									Please post some bookmarks and set tags!</p>
							</c:if>
						</div>
					</div>
				</div>
				<div class="page-content">
					<!-- =========================== -->
					<!-- Default page -->
					<!-- =========================== -->
					<div class="bookmarks show">
						<c:if test="${not empty bookmarks}">
							<c:forEach var="bookmark" items="${bookmarks}">
								<div class="bookmark">
									<div class="row">
										<div class="col-lg-12">
											<h3>
												<c:out value="${bookmark.title}" />
											</h3>
										</div>
									</div>
									<div class="row">
										<div class="col-lg-10">
											<a class="text-info" href="<c:out value="${bookmark.url}"/>">
												<c:out value="${bookmark.url}" />
											</a>
										</div>
										<div class="col-lg-2">
											<p>
												Last Updated:
												<c:out value="${bookmark.date}" />
											</p>
										</div>
									</div>
									<div class="row">
										<div class="col-lg-10">
											<p>
												<i class="fa fa-tags"></i> Tags:
												<c:forEach var="tag" items="${bookmark.tags}">
													<span class="dropdown"><code class="bookmark-tag">
															<c:set var="t" value="#" />
															<c:out value="${t}${tag}" />
														</code></span>
												</c:forEach>
											</p>
										</div>
										<div class="col-lg-2">
											<button class="btn btn-sm btn-success">
												<i class="fa fa-pencil-square-o"></i> Edit
											</button>
											<input type="hidden" name="bookmarkID" value="1">
										</div>
									</div>
								</div>
							</c:forEach>
						</c:if>
					</div>
					<!-- =========================== -->
					<!-- Click vào discover -->
					<!-- =========================== -->
					<div class="discover hidden">
						<div style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào Network -->
					<!-- =========================== -->
					<div class="network hidden">
						<div class="show" id="network-waiting" style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Add Link Modal -->
		<div class="modal fade" id="add-bookmark-modal" tabindex="-1"
			role="dialog" aria-labelledby="addBookmarkModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
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
						id="add-bookmark-form" action="dashboard/checkBookmark">
						<div class="modal-body">
							<div class="form-group">
								<form:label for="new-bookmark-textarea"
									class="control-label text-primary" path="url"
									id="new-bookmark-label">New Bookmark:</form:label>
								<form:textarea class="form-control" rows="5"
									id="new-bookmark-textarea" placeholder="Url Link" path="url"
									style="resize : none" />
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
			<div class="modal-dialog">
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
					<form method="post" role="form" id="add-tag-form"
						action="dashboard/addBookmark">
						<div class="modal-body">
							<div class="form-group">
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong>Url :</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-url"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong>Title :</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-title"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong> <i class="fa fa-tag"></i>Tags :
											</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<textarea class="form-control" id="bookmark-tags" rows="3"
											placeholder="Tags" style="resize: none" data-toggle="tooltip"
											data-placement="right"
											data-original-title="Tag are important part of organizing on Social Bookmarking Service. Using tags that are smart and simple benifits both you and community"></textarea>
										<p class="text-info">
											Recommend Tags <i class="fa fa-question"
												data-toggle="tooltip" data-placement="right"
												data-original-title="System use Topic Modeling to recommend tags to user"></i>
										</p>
										<div class="waiting-div"></div>
										<div id="recommend-tags-div"></div>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="form-group">
										<div class="col-xs-3">
											<p class="text-info">
												<i class="fa fa-comment"></i> <strong>Comment : </strong>
											</p>
										</div>
										<div class="col-xs-9">
											<textarea class="form-control" id="bookmark-comment" rows="4"
												placeholder="Your Comment" style="resize: none"
												data-toggle="tooltip" data-placement="right"
												data-original-title="What are you thinking about this Bookmark?"></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus"></i> Add Bookmark
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>

		<!-- Add Tag vào Bookmark của người dùng khác  -->
		<div class="modal fade" id="nw-add-tag-modal" tabindex="-1"
			role="dialog" aria-labelledby="nwAddTagModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
						<form role="form" id="nw-add-tag-form" class="form-inline">
							<div class="form-group has-feedback">
								<input type="text" id="nw-tag" class="form-control"
									placeholder="Tag" style="width: 510px;" />
							</div>
							<button type="submit" class="btn btn-info">Add</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script src="js/jquery.js"></script>
	<script src="js/jquery-ui.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/ScrollMagic.min.js"></script>
	<script src="js/metisMenu.min.js"></script>
	<script src="js/sb-admin-2.js"></script>
	<script src="js/bootbox.js"></script>
	<script src="js/panel.js"></script>
</body>
</html>