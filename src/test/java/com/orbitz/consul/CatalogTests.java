package com.orbitz.consul;

import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogNode;
import com.orbitz.consul.model.catalog.CatalogService;
import com.orbitz.consul.model.health.Node;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static com.orbitz.consul.query.QueryOptionsBuilder.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CatalogTests {

    @Test
    public void shouldGetNodes() throws UnknownHostException {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();

        assertEquals(1, catalogClient.getNodes().getResponse().size());
    }

    @Test
    public void shouldGetNodesByDatacenter() throws UnknownHostException {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();

        assertEquals(1, catalogClient.getNodes("dc1").getResponse().size());
    }

    @Test
    public void shouldGetNodesByDatacenterBlock() throws UnknownHostException {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();

        long start = System.currentTimeMillis();
        ConsulResponse<List<Node>> response = catalogClient.getNodes("dc1",
                builder().blockSeconds(2, Integer.MAX_VALUE).build());
        long time = System.currentTimeMillis() - start;

        assertTrue(time >= 2000);
        assertEquals(1, response.getResponse().size());
    }

    @Test
    public void shouldGetDatacenters() throws UnknownHostException {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();
        List<String> datacenters = catalogClient.getDatacenters();

        assertEquals(1, datacenters.size());
        assertEquals("dc1", datacenters.iterator().next());
    }

    @Test
    public void shouldGetServices() throws Exception {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();
        ConsulResponse<Map<String, List<String>>> services = catalogClient.getServices();

        assertTrue(services.getResponse().containsKey("consul"));
    }

    @Test
    public void shouldGetService() throws Exception {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();
        ConsulResponse<List<CatalogService>> services = catalogClient.getService("consul");

        assertEquals("consul", services.getResponse().iterator().next().getServiceName());
    }

    @Test
    public void shouldGetNode() throws Exception {
        Consul client = Consul.newClient();
        CatalogClient catalogClient = client.catalogClient();
        ConsulResponse<CatalogNode> node = catalogClient.getNode(catalogClient.getNodes()
                .getResponse().iterator().next().getNode());

        assertNotNull(node);
    }
}