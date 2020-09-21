# analytic-webradio

This project is done to download and import Icecast server log into a MySql Database for analytic purpose. It is supposed to work with one of the biggest Italian server site.

**Feature**

- Use Selenium to download a zip file of logs
- Unzip only access and error files
- parse log files and insert into DB


NOTE:
In the access table the date of insert is the start date of a single session of listening