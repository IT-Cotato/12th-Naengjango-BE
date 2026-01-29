INSERT INTO agreement
(name, is_required, content, version, created_at, updated_at)
VALUES
    ('이용 약관 동의', b'1', '서비스 이용을 위한 필수 약관입니다.', 'v1.0', NOW(), NOW()),
    ('개인정보 수집 및 이용 동의', b'1', '개인정보 수집 및 이용에 대한 안내입니다.', 'v1.0', NOW(), NOW()),
    ('SNS 알림 허용', b'0', '이벤트 및 알림 수신 동의입니다.', 'v1.0', NOW(), NOW());
