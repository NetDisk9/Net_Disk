local value_key=KEYS[1]
local time_key=KEYS[2]
local time_interval=tonumber(ARGV[1])
local token_size=tonumber(ARGV[2])
local need_token_count=tonumber(ARGV[3])
local current_key_time=tonumber(redis.call('get',time_key) or 0)
local current_time=tonumber(redis.call('TIME')[1])
local last_token_count=tonumber(redis.call('get',value_key) or 0)
if current_key_time + time_interval <= current_time then
    last_token_count=token_size
    redis.call('set',value_key,token_size)
    redis.call('set',time_key,current_time)
end
if last_token_count < need_token_count then
    redis.call('set',value_key,0)
    return last_token_count
end
redis.call('set',value_key,last_token_count-need_token_count);
return need_token_count;