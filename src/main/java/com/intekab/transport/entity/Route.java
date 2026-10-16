package com.intekab.transport.entity;

import com.intekab.transport.enums.RouteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String routeName;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status = RouteStatus.ACTIVE;

    protected Route() {
        // required by JPA
    }

    public Route(String routeName, String startLocation, String endLocation, Double distanceKm) {
        this.routeName = routeName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distanceKm = distanceKm;
        this.status = RouteStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public RouteStatus getStatus() {
        return status;
    }

    public void setStatus(RouteStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route)) return false;
        return id != null && id.equals(route.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
