package com.bwgjoseph.h2jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Starter implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    // note that this implementation is not complete
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Grab all the mappers
        List<Mapper> mappers = this.findAllMapper();

        // Map to Person
        // It can be a generic class instead of Person so that it can be table agnostic?
        List<Person> persons = mappers.stream()
            .map(this::findPerson)
            .flatMap(List::stream)
            .toList();

        // Run the input through the conversion process
        List<Output> outputs = persons.stream()
            .map(this::conversion)
            .toList();

        // Once converted, update before to the mapper table
        outputs.forEach(this::update);
    }

    public void update(Output output) {
        this.jdbcTemplate.update("update mapper set output = ? where id = ?", output.output(), output.mapperId());
    }

    private Output conversion(Person p) {
        return new Output(p.getMapperId(), String.valueOf(p.getDob().toString()));
    }

    public List<Mapper> findAllMapper() {
        return this.jdbcTemplate.query("select * from mapper", new MapperRowMapper());
    }

    static class MapperRowMapper implements RowMapper<Mapper> {

        @Override
        public Mapper mapRow(ResultSet rs, int numRow) throws SQLException {
            return Mapper.builder()
                .id(rs.getInt("id"))
                .tableName(rs.getString("table_name"))
                .dateCol(rs.getString("date_col"))
                .accCol(rs.getString("acc_col"))
                .build();
        }
    }

    public List<Person> findPerson(Mapper mapper) {
        String query = String.format("select id, %s, %s from %s",
            mapper.getDateCol(),
            mapper.getAccCol(),
            mapper.getTableName());

        log.info("query {}", query);

        return this.jdbcTemplate.query(query, new PersonRowMapper());
    }

    static class PersonRowMapper implements RowMapper<Person> {

        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Person.builder()
                .mapperId(rs.getInt("id"))
                .dob(rs.getDate("dob").toLocalDate())
                .da(rs.getInt("da"))
                .build();
        }
    }
}

record Output(int mapperId, String output) {}