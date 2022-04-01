package ru.job4j.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class Rabbit implements Job {

    private void rabbitAdd(Connection connection) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try (PreparedStatement ps = connection.prepareStatement(
                "insert into rabbit(created) values(?)"
        )) {
            ps.setTimestamp(1, timestamp);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Rabbit runs here ...");
        List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
        Connection connection = (Connection) context.getJobDetail()
                .getJobDataMap()
                .get("connection");
        store.add(System.currentTimeMillis());
        rabbitAdd(connection);
    }
}