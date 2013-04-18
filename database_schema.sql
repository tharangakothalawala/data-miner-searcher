-- phpMyAdmin SQL Dump
-- version 3.2.0.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 18, 2013 at 03:48 PM
-- Server version: 5.1.37
-- PHP Version: 5.3.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `fproject_test`
--

-- --------------------------------------------------------

--
-- Table structure for table `fproject_comments`
--

CREATE TABLE IF NOT EXISTS `fproject_comments` (
  `comment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `image_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `user_name` varchar(100) NOT NULL DEFAULT '',
  `comment_headline` varchar(255) NOT NULL DEFAULT '',
  `comment_text` text NOT NULL,
  `comment_ip` varchar(20) NOT NULL DEFAULT '',
  `comment_date` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `image_id` (`image_id`),
  KEY `user_id` (`user_id`),
  KEY `comment_date` (`comment_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=78 ;

-- --------------------------------------------------------

--
-- Table structure for table `fproject_images`
--

CREATE TABLE IF NOT EXISTS `fproject_images` (
  `image_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cat_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `image_name` varchar(255) NOT NULL DEFAULT '',
  `image_description` text NOT NULL,
  `image_keywords` text NOT NULL,
  `image_date` int(11) unsigned NOT NULL DEFAULT '0',
  `image_active` tinyint(1) NOT NULL DEFAULT '1',
  `image_media_file` varchar(255) NOT NULL DEFAULT '',
  `image_thumb_file` varchar(255) NOT NULL DEFAULT '',
  `image_download_url` varchar(255) NOT NULL DEFAULT '',
  `image_allow_comments` tinyint(1) NOT NULL DEFAULT '1',
  `image_comments` int(10) unsigned NOT NULL DEFAULT '0',
  `image_downloads` int(10) unsigned NOT NULL DEFAULT '0',
  `image_votes` int(10) unsigned NOT NULL DEFAULT '0',
  `image_rating` decimal(4,2) NOT NULL DEFAULT '0.00',
  `image_hits` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`image_id`),
  KEY `cat_id` (`cat_id`),
  KEY `user_id` (`user_id`),
  KEY `image_date` (`image_date`),
  KEY `image_active` (`image_active`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5334 ;

-- --------------------------------------------------------

--
-- Table structure for table `fproject_users`
--

CREATE TABLE IF NOT EXISTS `fproject_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_level` int(11) NOT NULL DEFAULT '1',
  `user_name` varchar(255) NOT NULL DEFAULT '',
  `user_password` varchar(255) NOT NULL DEFAULT '',
  `user_email` varchar(255) NOT NULL DEFAULT '',
  `user_showemail` tinyint(1) NOT NULL DEFAULT '0',
  `user_allowemails` tinyint(1) NOT NULL DEFAULT '1',
  `user_invisible` tinyint(1) NOT NULL DEFAULT '0',
  `user_joindate` int(11) unsigned NOT NULL DEFAULT '0',
  `user_activationkey` varchar(32) NOT NULL DEFAULT '',
  `user_lastaction` int(11) unsigned NOT NULL DEFAULT '0',
  `user_location` varchar(255) NOT NULL DEFAULT '',
  `user_lastvisit` int(11) unsigned NOT NULL DEFAULT '0',
  `user_comments` int(10) unsigned NOT NULL DEFAULT '0',
  `user_homepage` varchar(255) NOT NULL DEFAULT '',
  `user_icq` varchar(20) NOT NULL DEFAULT '',
  `user_lightbox` mediumint(8) unsigned NOT NULL,
  `user_lightbox_count` smallint(3) NOT NULL DEFAULT '0',
  `user_lightbox_private` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `user_support` tinyint(1) NOT NULL DEFAULT '1',
  `user_t_images` smallint(6) NOT NULL DEFAULT '0',
  `user_moderador_categorias` tinyint(1) NOT NULL DEFAULT '0',
  `user_moderador_imagenes` tinyint(1) NOT NULL DEFAULT '0',
  `user_moderador_comentarios` tinyint(1) NOT NULL DEFAULT '0',
  `user_moderador_usuarios` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  KEY `user_lastaction` (`user_lastaction`),
  KEY `user_name` (`user_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=495 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `fproject_comments`
--
ALTER TABLE `fproject_comments`
  ADD CONSTRAINT `fproject_comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fproject_users` (`user_id`);

--
-- Constraints for table `fproject_images`
--
ALTER TABLE `fproject_images`
  ADD CONSTRAINT `fproject_images_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fproject_users` (`user_id`);
