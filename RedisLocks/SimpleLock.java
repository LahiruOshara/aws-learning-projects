class SimpleLock {
    public setupSimpleLock() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379"); // user your redis server address

        RedissonClient client = Redisson.create(config);
        RLock lock = client.getLock("custom-lock");
        try {
            lock.lock();
            // Critical section
        } finally {
            lock.unlock();
        }
    }
}

