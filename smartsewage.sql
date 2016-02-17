use smartsewage;
create table sensor_log(
seqnum int not null auto_increment,
PsID int,
level int,
time datetime,
primary key (seqnum)
);
