UPDATE email_template
	SET body='<h3>Dear [NAME] ,</h3> <h3>Your Record Label has invited you to join MBox.</h3> <h3>M Box is a web platform where you can customize a page with all your music.</h3> <h3> <a href="[APPURL]">Click here to get started</a> <h3> After setting up your password you can login with [EMAILADRESS]</h3> </h3> <br> <h3>Regards, </h3>  <h3>M Box</h3>'
	WHERE id=1;

UPDATE email_template
	SET body='<h3>Dear [NAME],</h3> <h3>Your M Box account has been created!</h3> <h3><a href="[APPURL]">Click here to set your password</a> <h3> After setting up your password you can login with [EMAILADRESS]</h3> <br> <h3>Regards, </h3>  <h3>M Box</h3>'
	WHERE id=5;

delete from user_roles where user_id in
(SELECT u.id
FROM users u
WHERE ID < (SELECT MAX(ID) FROM users b WHERE u.email=b.email GROUP BY email HAVING COUNT(*) > 1));

delete from verification_token where user_id in
(SELECT u.id
FROM users u
WHERE ID < (SELECT MAX(ID) FROM users b WHERE u.email=b.email GROUP BY email HAVING COUNT(*) > 1));

delete from users where id in
(SELECT u.id
FROM users u
WHERE ID < (SELECT MAX(ID) FROM users b WHERE u.email=b.email GROUP BY email HAVING COUNT(*) > 1));

ALTER TABLE users ADD CONSTRAINT UC_EMAIL UNIQUE (email);