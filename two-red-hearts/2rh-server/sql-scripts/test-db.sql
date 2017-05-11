CREATE DATABASE 2rh;
USE 2rh;

SET GLOBAL event_scheduler = ON;

SOURCE init.sql;

/*
All passwords are 1234
All tokens are: 1bf120a2e90fb6500eb317ef347ca3b1de0190ed1d697ac22c94568a3a40cf8a
*/
INSERT INTO User (username, email, token, salt, hash, name, majorCode, genderCode)
VALUES
  ('t1',
   'test1@ggmail.com',
   '62fee3ac8c2131541c67cac72680a4623eff645b1d709030f42fc2f0317b89df',
   '93d6e03d919f70d9',
   '6ceffe871638a97981ca6a297962fc0ed2f804718076422026a9fb84f7a9e7c15b6f77ad33238a83ebd5a13ed821e6742755c16b6e44069af1babf85c8f0c23d',
   'Test User One',
   '0',
   'f'),

  ('t2',
   'test2@dajizi.org',
   '6753561a999312c7726553166bdb8aa1790df70b3bef72d5962c13c31ad06bb7',
   'e2811a92f2550530',
   '29f420684d3726268802e13fc64863776af6bec1d37fdfe4357b6f7cca7f03a42b4af5c00dcc33c938d7100990082d397aa85a3af2c3c3a14867c48278900ab9',
   'Test User Two',
   '23',
   'm'),

  ('t3',
   'test3@dajizi.org',
   '953c33c443a6cfe2c07fb22872e477a0ea27e69ee31cb71d89fa3c73a2aa73fc',
   '449d8cb5830b0140',
   '8bc5857d22443502ceb414ab55fa1e94404ecd3a1840cf87500498812456b9064c1c87aedae0ad23f1f0e1554bd387b26f710ec091ac7a0b36bff2f5e6796bb5',
   '测试用户三号',
   '23',
   'm'),

  ('t4',
   'test4@jsiefj.fsiejf',
   '28978f86f582d7825d984ab9e0b80fa0d2c2f14c71ab7a8f37d6e73e558f98ef',
   '449d8cb5830b0140',
   '8bc5857d22443502ceb414ab55fa1e94404ecd3a1840cf87500498812456b9064c1c87aedae0ad23f1f0e1554bd387b26f710ec091ac7a0b36bff2f5e6796bb5',
   '測試專用 用戶四號',
   '46',
   'o'),

  ('t5',
   'test5@example.com',
   '328a3c31623ff49ede0684e75abe2750fc113b1c8bca937e068ce61d3fccc88e',
   '1339363a068e71d6',
   'bd902010650f70fe10b0c8af3cb107880270802c6165d26b752b87e8869b542945d886f9d0dc5694a43e3067e47757f429b34a94d95354a531593a90ca4516f2',
   '五號機',
   '100',
   'f'),

  ('t6',
   'test6@yahooooo.org',
   'cc88fe445acf7aa938fcfab7a1887a764a949cb462f63ff0c452d69452d4b397',
   '4fc2abdf84a542a1',
   '2733e9e0de2dfd3d0484f9db4ce8c12215e1a0a3eebc5ab8aa8992652cebd6e63f0778c6d2dca434b6195f994a4093fbfde8a6591280a1ed56981299dcdd54ae',
   'テスト 6番',
   '42',
   'm'),

  ('t7',
     'test7@yahooooo.org',
     'b0068aa615c74ebab958300fecb600933d9076f12aeee431ac29120ad67c62e2',
     '',
     '',
     'Doruk Dixon',
     '178',
     'f'),

  ('t8',
      'test8@yahooooo.org',
      '13c3a17a5a40e1055e5a1005051bf34cbeedb1b403e1e04393104d2c5f6944a0',
      '',
      '',
      'Sargis Doherty',
      '157',
      'm'),

  ('t9',
      'test9@yahooooo.org',
      '7a1658a0f0238075eca1d99c36872d2e370d5e785fbdcfb7750f8541eb182486',
      '',
      '',
      'Hyperion Attwood',
      '91',
      'm'),

  ('t10',
      'test10@yahooooo.org',
      'a6e681e10ec60ed795bd2cc4c04762b5aa421de0a9ce32d2df5a402f9a4700ca',
      '',
      '',
      'Fikri Mertens',
      '74',
      'f'),

  ('t11',
      'test11@yahooooo.org',
      'fcbc9af2ca8d4ada16a935e540869dd7af1df9db63fa59f3456c7828d6c5f925',
      '',
      '',
      'Valery Sexton',
      '171',
      'f'),

  ('t12',
      'test12@yahooooo.org',
      '8c6191c4f25375fc4f70e65579df7409d147bb6b3be6d875ab41bb3d21a74423',
      '',
      '',
      'Mervyn Veres',
      '102',
      'f'),

  ('t13',
     'test13@yahooooo.org',
     '13a475d9cef4acb3344f4ff885797f3ae8a6f1abcf8a2ac0a4b5c72eb1b5e65a',
     '',
     '',
     'Hakan Adelardi',
     '46',
     'm'),

  ('t14',
     'test14@yahooooo.org',
     '1bf120a2e90fb6500eb317ef347ca3b1de0190ed1d697ac22c94568a3a40cf8a',
     '',
     '',
     'Viraj Ryer',
     '100',
     'm'),

  ('t15',
     'test15@yahooooo.org',
     '45480a95c5d9de4c099813d685e1d7769a13853195a03ee703d6ec48bf35113e',
     '',
     '',
     'Awotwi Priddy',
     '171',
     'm'),

  ('t16',
      'test16@yahooooo.org',
      '0fd3db38c00fc1bc5d0783f62aa5a1750b12fd78f1db7cbc4d9b99c3f5b15b3a',
      '',
      '',
      'Doron Wong',
      '154',
      'm'),

  ('t17',
       'test17@yahooooo.org',
       '0189539d0fc24df6f086f990af1a64f2ee3e3b207cb2bc6e586e02b6de04bb0d',
       '',
       '',
       'Chanda Graner',
       '100',
       'm'),

  ('t18',
       'test18@yahooooo.org',
       'd391ca3b2fb5f22abec019dbdf7dec49a9ee2ee172d9e704304d3dbcf1fcb97e',
       '',
       '',
       'Janus Patenaude',
       '64',
       'f'),

  ('t19',
       'test19@yahooooo.org',
       '805fe2bd6d971470268f02d1b4fe986ecbab7fee258daeeac43bc7ae6ccd7376',
       '',
       '',
       'Konrad Wegner',
       '0',
       'f'),

  ('t20',
     'test20@yahooooo.org',
     '6034df6757efd5a7ce17ac8dabb23b1d26f7d097ef6b9578e929103d943b8cf6',
     '',
     '',
     'テスト 6番',
     '42',
     'm');

SOURCE locations.sql;
SOURCE hobby-tags.sql;

CALL AddFollow(5,9);
CALL AddFollow(3,15);
CALL AddFollow(12,1);
CALL AddFollow(8,13);
CALL AddFollow(4,8);
CALL AddFollow(16,9);
CALL AddFollow(13,4);
CALL AddFollow(18,4);
CALL AddFollow(13,17);
CALL AddFollow(8,19);
CALL AddFollow(11,14);
CALL AddFollow(16,14);
CALL AddFollow(15,19);
CALL AddFollow(10,3);
CALL AddFollow(7,2);
CALL AddFollow(18,1);
CALL AddFollow(16,17);
CALL AddFollow(16,7);
CALL AddFollow(14,5);
CALL AddFollow(2,9);
CALL AddFollow(12,13);
CALL AddFollow(9,15);
CALL AddFollow(7,6);
CALL AddFollow(10,7);
CALL AddFollow(5,18);
CALL AddFollow(8,10);
CALL AddFollow(10,12);
CALL AddFollow(18,16);
CALL AddFollow(14,1);
CALL AddFollow(2,13);
CALL AddFollow(11,1);
CALL AddFollow(1,10);
CALL AddFollow(11,18);
CALL AddFollow(8,16);
CALL AddFollow(6,15);
CALL AddFollow(16,18);
CALL AddFollow(13,11);
CALL AddFollow(16,13);
CALL AddFollow(3,2);
CALL AddFollow(11,4);
CALL AddFollow(18,2);
CALL AddFollow(14,19);
CALL AddFollow(15,3);
CALL AddFollow(8,11);
CALL AddFollow(11,9);
CALL AddFollow(5,2);
CALL AddFollow(17,1);
CALL AddFollow(18,17);
CALL AddFollow(2,10);
CALL AddFollow(9,10);
CALL AddFollow(13,6);
CALL AddFollow(8,7);
CALL AddFollow(17,13);
CALL AddFollow(16,19);
CALL AddFollow(13,10);
CALL AddFollow(17,4);
CALL AddFollow(18,10);
CALL AddFollow(17,18);
CALL AddFollow(11,15);
CALL AddFollow(11,17);
CALL AddFollow(18,3);
CALL AddFollow(17,9);
CALL AddFollow(3,12);
CALL AddFollow(4,6);
CALL AddFollow(9,2);
CALL AddFollow(16,8);
CALL AddFollow(4,17);
CALL AddFollow(11,3);
CALL AddFollow(16,1);
CALL AddFollow(14,12);
CALL AddFollow(15,6);
CALL AddFollow(10,14);
CALL AddFollow(13,18);
CALL AddFollow(12,4);
CALL AddFollow(1,7);
CALL AddFollow(13,9);
CALL AddFollow(7,17);
CALL AddFollow(15,16);


CALL AddUserHobby(16,6);
CALL AddUserHobby(5,9);
CALL AddUserHobby(10,11);
CALL AddUserHobby(12,1);
CALL AddUserHobby(4,8);
CALL AddUserHobby(5,6);
CALL AddUserHobby(7,12);
CALL AddUserHobby(2,8);
CALL AddUserHobby(16,2);
CALL AddUserHobby(12,5);
CALL AddUserHobby(4,12);
CALL AddUserHobby(2,12);
CALL AddUserHobby(11,14);
CALL AddUserHobby(3,7);
CALL AddUserHobby(14,9);
CALL AddUserHobby(15,5);
CALL AddUserHobby(12,9);
CALL AddUserHobby(7,2);
CALL AddUserHobby(18,1);
CALL AddUserHobby(10,8);
CALL AddUserHobby(13,12);
CALL AddUserHobby(8,14);
CALL AddUserHobby(4,9);
CALL AddUserHobby(5,5);
CALL AddUserHobby(16,10);
CALL AddUserHobby(13,3);
CALL AddUserHobby(7,6);
CALL AddUserHobby(16,3);
CALL AddUserHobby(1,14);
CALL AddUserHobby(4,4);
CALL AddUserHobby(5,12);
CALL AddUserHobby(13,7);
CALL AddUserHobby(7,10);
CALL AddUserHobby(2,2);
CALL AddUserHobby(3,6);
CALL AddUserHobby(12,10);
CALL AddUserHobby(19,10);
CALL AddUserHobby(17,14);
CALL AddUserHobby(1,1);
CALL AddUserHobby(11,13);
CALL AddUserHobby(6,4);
CALL AddUserHobby(5,4);
CALL AddUserHobby(13,2);
CALL AddUserHobby(10,4);
CALL AddUserHobby(7,1);
CALL AddUserHobby(17,10);
CALL AddUserHobby(12,7);
CALL AddUserHobby(11,9);
CALL AddUserHobby(17,1);
CALL AddUserHobby(15,10);
CALL AddUserHobby(14,6);
CALL AddUserHobby(9,10);
CALL AddUserHobby(4,14);
CALL AddUserHobby(19,2);
CALL AddUserHobby(3,9);
CALL AddUserHobby(2,3);
CALL AddUserHobby(15,7);
CALL AddUserHobby(1,9);
CALL AddUserHobby(2,14);
CALL AddUserHobby(13,10);
CALL AddUserHobby(6,5);
CALL AddUserHobby(18,10);
CALL AddUserHobby(16,12);
CALL AddUserHobby(7,9);
CALL AddUserHobby(14,11);
CALL AddUserHobby(19,9);
CALL AddUserHobby(2,7);
CALL AddUserHobby(3,5);
CALL AddUserHobby(15,2);
CALL AddUserHobby(9,13);
CALL AddUserHobby(13,14);
CALL AddUserHobby(6,1);
CALL AddUserHobby(3,1);
CALL AddUserHobby(18,14);
CALL AddUserHobby(11,3);
CALL AddUserHobby(16,8);
CALL AddUserHobby(19,5);
CALL AddUserHobby(7,4);
CALL AddUserHobby(10,14);
CALL AddUserHobby(6,13);
CALL AddUserHobby(13,9);
CALL AddUserHobby(19,1);
CALL AddUserHobby(9,12);
