package odata4j.jpa;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import odata4j.edm.EdmDataServices;
import odata4j.producer.EntitiesRequest;
import odata4j.producer.EntitiesResponse;
import odata4j.producer.EntityRequest;
import odata4j.producer.EntityResponse;
import odata4j.producer.ODataProducer;
import odata4j.producer.ODataProducerFactory;

public class JPABackendFactory implements ODataProducerFactory {

	@Override
	public ODataProducer create(Properties properties) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("NorthwindService");
		return new JPAProducer( emf, "Northwind");
	}

	

}
