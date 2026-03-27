-- 修复头像/昵称保存失败（Data too long 等）：放宽字段长度
-- 请在库 campus_lost_found 上执行（与 application.yml 一致）

ALTER TABLE users MODIFY COLUMN nickname VARCHAR(64) NOT NULL;

ALTER TABLE users MODIFY COLUMN avatar_url VARCHAR(512) NULL;
