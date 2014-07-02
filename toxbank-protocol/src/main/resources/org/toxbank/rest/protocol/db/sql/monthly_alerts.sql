insert into alert
SELECT null,
"ToxBank Monthly alert",
"?metadata=*&resourceType=protocol&resourceType=investigation",
"FREETEXT",
"monthly",
1,
iduser,
now(),
concat(year(now())-1,'-',month(now()),'-1 0:0:0.0')
 FROM user
 ;