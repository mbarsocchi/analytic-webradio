CREATE TABLE `access` (
  `date` timestamp NOT NULL,
  `hash` text NOT NULL,
  `ip` varchar(30) NOT NULL,
  `user_agent` text NOT NULL,
  `refer` text NOT NULL,
  `listening_seconds` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE `listner` (
  `date` timestamp NOT NULL,
  `listner_number` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

