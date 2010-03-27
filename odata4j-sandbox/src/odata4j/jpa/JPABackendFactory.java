package odata4j.jpa;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import odata4j.producer.ODataProducer;
import odata4j.producer.ODataProducerFactory;

public class JPABackendFactory implements ODataProducerFactory {

	@Override
	public ODataProducer create(Properties properties) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("NorthwindService");
		return new JPAProducer( emf, "Northwind");
	}

	

}
