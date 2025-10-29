package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OrderFileDao implements OrderDao{

    private final Path ordersDirectory;
    private final DateTimeFormatter fileDate = DateTimeFormatter.ofPattern("MMddyyyy");
    private final DateTimeFormatter exportDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    // In-memory cache for session
    private final Map<LocalDate, List<Order>> cache = new HashMap<>();

    public OrderFileDao(String ordersDir) {
        this.ordersDirectory = Path.of(ordersDir);
        try { Files.createDirectories(ordersDirectory); } catch (IOException ignored) {}
    }

    private Path fileForDate(LocalDate date) {
        String name = "Orders_" + date.format(fileDate) + ".txt";
        return ordersDirectory.resolve(name);
    }

    private void loadDate(LocalDate date) throws DaoException {

        if (cache.containsKey(date)) return;

        Path p = fileForDate(date);
        List<Order> list = new ArrayList<>();

        if (!Files.exists(p)) { cache.put(date, list); return; }

        try (BufferedReader br = Files.newBufferedReader(p)) {

            String header = br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                Order o = new Order();
                o.setOrderNumber(Integer.parseInt(parts[0]));
                o.setCustomerName(parts[1]);
                o.setState(parts[2]);
                o.setTaxRate(new BigDecimal(parts[3]));
                o.setProductType(parts[4]);
                o.setArea(new BigDecimal(parts[5]));
                o.setCostPerSquareFoot(new BigDecimal(parts[6]));
                o.setLaborCostPerSquareFoot(new BigDecimal(parts[7]));
                o.setMaterialCost(new BigDecimal(parts[8]));
                o.setLaborCost(new BigDecimal(parts[9]));
                o.setTax(new BigDecimal(parts[10]));
                o.setTotal(new BigDecimal(parts[11]));
                o.setOrderDate(date);
                list.add(o);
            }

            cache.put(date, list);

        } catch (Exception e) {
            throw new DaoException("Could not load orders for date: " + date, e);
        }
    }

    private void persistDate(LocalDate date) throws DaoException {

        List<Order> list = cache.getOrDefault(date, new ArrayList<>());
        Path p = fileForDate(date);

        try (BufferedWriter bw = Files.newBufferedWriter(p)) {

            bw.write("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");
            bw.newLine();

            for (Order o : list) {
                bw.write(String.join(",",
                        String.valueOf(o.getOrderNumber()),
                        o.getCustomerName(),
                        o.getState(),
                        o.getTaxRate().toPlainString(),
                        o.getProductType(),
                        o.getArea().toPlainString(),
                        o.getCostPerSquareFoot().toPlainString(),
                        o.getLaborCostPerSquareFoot().toPlainString(),
                        o.getMaterialCost().toPlainString(),
                        o.getLaborCost().toPlainString(),
                        o.getTax().toPlainString(),
                        o.getTotal().toPlainString()
                ));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new DaoException("Could not save orders for date: " + date, e);
        }
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DaoException {

        loadDate(date);

        return Collections.unmodifiableList(cache.getOrDefault(date, new ArrayList<>()));
    }

    @Override
    public Order addOrder(Order order) throws DaoException {

        LocalDate date = order.getOrderDate();
        loadDate(date);
        List<Order> list = cache.get(date);
        int next = list.stream().mapToInt(Order::getOrderNumber).max().orElse(0) + 1;
        order.setOrderNumber(next);
        list.add(order);
        persistDate(date);

        return order;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws DaoException {

        loadDate(date);
        List<Order> list = cache.get(date);
        Optional<Order> existing = list.stream().filter(o -> o.getOrderNumber() == order.getOrderNumber()).findFirst();

        if (existing.isEmpty()) return null;

        list.remove(existing.get());
        list.add(order);
        persistDate(date);

        return order;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws DaoException {

        loadDate(date);
        List<Order> list = cache.get(date);
        Optional<Order> existing = list.stream().filter(o -> o.getOrderNumber() == orderNumber).findFirst();

        if (existing.isEmpty()) return null;

        Order removed = existing.get();
        list.remove(removed);
        persistDate(date);

        return removed;
    }

    @Override
    public void exportAll(String exportFilePath) throws DaoException {

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(exportFilePath))) {

            bw.write("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,Date");
            bw.newLine();

            // find all files in directory
            var files = Files.list(ordersDirectory).filter(p -> p.getFileName().toString().startsWith("Orders_")).collect(Collectors.toList());

            for (Path p : files) {
                String name = p.getFileName().toString();
                String dateStr = name.substring("Orders_".length(), name.length() - 4); // MMDDYYYY
                LocalDate d = LocalDate.parse(dateStr, fileDate);
                loadDate(d);

                for (Order o : cache.getOrDefault(d, List.of())) {
                    bw.write(String.join(",",
                            String.valueOf(o.getOrderNumber()),
                            o.getCustomerName(),
                            o.getState(),
                            o.getTaxRate().toPlainString(),
                            o.getProductType(),
                            o.getArea().toPlainString(),
                            o.getCostPerSquareFoot().toPlainString(),
                            o.getLaborCostPerSquareFoot().toPlainString(),
                            o.getMaterialCost().toPlainString(),
                            o.getLaborCost().toPlainString(),
                            o.getTax().toPlainString(),
                            o.getTotal().toPlainString(),
                            d.format(exportDate)
                    ));
                    bw.newLine();
                }
            }

        } catch (Exception e) {
            throw new DaoException("Could not export data", e);
        }
    }
}
