class FairLock {
    public setupFairLock() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379"); // user your redis server address

        RedissonClient client = Redisson.create(config);
        RLock lock = redisson.getFairLock("anyLock"); // get fair lock instead of the simple lock
        try {
            lock.lock();
            // Critical section
        } finally {
            lock.unlock();
        }
    }
}