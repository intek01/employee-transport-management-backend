package com.intekab.transport.repository;

import com.intekab.transport.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    boolean existsByRouteName(String routeName);
}
