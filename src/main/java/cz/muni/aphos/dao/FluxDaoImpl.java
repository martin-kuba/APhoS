package cz.muni.aphos.dao;

import cz.muni.aphos.dto.FluxUserTime;
import cz.muni.aphos.mappers.FluxApiMapper;
import cz.muni.aphos.mappers.FluxRowMapper;
import cz.muni.aphos.openapi.models.Flux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

/**
 * The Data Access Object implementation for the flux entity.
 */
@Repository
@Transactional
public class FluxDaoImpl implements FluxDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public long saveFlux(String recString, String decString, Float rec,
                         Float dec, Float apertureAuto, Float apertureAutoDev, Long spaceObjectId,
                         String userId, Long photoId, Float[] apertures, Float[] apertureDevs) {
        String insertQuery = "INSERT INTO flux " +
                "(id, rec, dec, coordinates, ap_auto, ap_auto_dev, object_id, user_id, " +
                "photo_properties_id, apertures, aperture_devs)" +
                "VALUES (nextval('flux_id_seq'), ?, ?, ll_to_earth(?, ?) ,?, ?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertQuery, new String[]{"id"});
            ps.setString(1, recString);
            ps.setString(2, decString);
            ps.setFloat(3, dec);
            ps.setFloat(4, rec);
            ps.setFloat(5, apertureAuto);
            ps.setFloat(6, apertureAutoDev);
            if (spaceObjectId != null) {
                ps.setLong(7, spaceObjectId);
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, userId);
            ps.setLong(9, photoId);
            ps.setArray(10, connection.createArrayOf("float8", apertures));
            ps.setArray(11, connection.createArrayOf("float8", apertureDevs));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<FluxUserTime> getFluxesByObjId(Long originalObjectId, Long referenceObjectId) {
        String query = "SELECT rec, dec, OF.ap_auto, OF.apertures, RF.ap_auto ref_ap_auto, " +
                "OF.ap_auto_dev, OF.aperture_devs, RF.ap_auto_dev ref_ap_auto_dev, RF.aperture_devs ref_ap_devs, " +
                "RF.apertures ref_apertures, username, exposure_begin, exposure_end, google_sub FROM " +
               "(SELECT * FROM flux WHERE object_id=?) AS OF INNER JOIN " +
        "(SELECT ap_auto, apertures, ap_auto_dev, aperture_devs, photo_properties_id " +
                "FROM flux WHERE object_id=?) AS RF " +
        "ON OF.photo_properties_id=RF.photo_properties_id INNER JOIN users ON users.google_sub=user_id " +
        "INNER JOIN photo_properties on OF.photo_properties_id=photo_properties.id";
        return jdbcTemplate.query(query, new FluxRowMapper(), originalObjectId, referenceObjectId);
    }

    @Override
    public long fluxExists(Long id) {
        // method for testing purposes
        String query = "SELECT id FROM flux WHERE id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, id);
    }

    @Override
    public List<Flux> getFluxesByObj(Long id) {
        String query = "SELECT * FROM flux " +
                "INNER JOIN users ON users.google_sub=user_id INNER JOIN " +
                "photo_properties on photo_properties.id=photo_properties_id WHERE object_id=? LIMIT 2000";
        return jdbcTemplate.query(query, new FluxApiMapper(), id);
    }

    @Override
    public Long getNumberOfFluxesEstimate() {
        return jdbcTemplate
                .queryForObject("SELECT reltuples FROM pg_class WHERE relname = 'flux'", Long.class);
    }
}
