package net.cosmogrp.crclans.redis;

import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisBuilder {

    private String host;
    private int port;
    private String password;

    private int timeout;
    private JedisPoolConfig config = new JedisPoolConfig();

    protected JedisBuilder() {

    }

    public JedisBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public JedisBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public JedisBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JedisBuilder setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public JedisBuilder setConfig(JedisPoolConfig config) {
        this.config = config;
        return this;
    }

    public JedisInstance build() {
        Jedis jedis = new Jedis(host, port, timeout);

        JedisPool jedisPool;
        if (password == null || password.trim().isEmpty()) {
            jedisPool = new JedisPool(config, host, port, timeout);
        } else {
            jedisPool = new JedisPool(config, host, port, timeout, password);
            jedis.auth(password);
        }

        return new JedisInstance(jedis, jedisPool);
    }

    public static JedisBuilder builder() {
        return new JedisBuilder();
    }

    public static JedisBuilder fromConfig(ConfigurationSection section) {
        return builder()
                .setHost(section.getString("host", "localhost"))
                .setPort(section.getInt("port", 6379))
                .setPassword(section.getString("password", ""))
                .setTimeout(section.getInt("timeout", 2000));
    }

}
