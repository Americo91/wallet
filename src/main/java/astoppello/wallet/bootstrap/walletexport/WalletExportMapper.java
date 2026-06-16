package astoppello.wallet.bootstrap.walletexport;

import astoppello.wallet.model.CategoryType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Mapper
public abstract class WalletExportMapper {

    private static final Map<CategoryTypeRecord, String> categoryMap = new HashMap<>();

    static {
        //Food & Drinks
        categoryMap.put(new CategoryTypeRecord("Food & Drinks", CategoryType.EXPENSE), "Food & Dining");
        categoryMap.put(new CategoryTypeRecord("Food & Dining", CategoryType.EXPENSE), "Food & Dining");
        categoryMap.put(new CategoryTypeRecord("Bar, cafe", CategoryType.EXPENSE), "Coffee & Drinks");
        categoryMap.put(new CategoryTypeRecord("Coffee & Drinks", CategoryType.EXPENSE), "Coffee & Drinks");
        categoryMap.put(new CategoryTypeRecord("Restaurants", CategoryType.EXPENSE), "Restaurants");
        categoryMap.put(new CategoryTypeRecord("Restaurant, fast-food", CategoryType.EXPENSE), "Restaurants");
        categoryMap.put(new CategoryTypeRecord("Groceries", CategoryType.EXPENSE), "Groceries");

        // Shopping
        categoryMap.put(new CategoryTypeRecord("Drug-store, chemist", CategoryType.EXPENSE), "Pharmacy");
        categoryMap.put(new CategoryTypeRecord("Drugstore", CategoryType.EXPENSE), "Pharmacy");
        categoryMap.put(new CategoryTypeRecord("Shopping", CategoryType.EXPENSE), "Shopping");
        categoryMap.put(new CategoryTypeRecord("Lesiure time", CategoryType.EXPENSE), "Hobbies");
        categoryMap.put(new CategoryTypeRecord("Stationery, tools", CategoryType.EXPENSE), "Household Supplies");
        categoryMap.put(new CategoryTypeRecord("Cancelleria & Utensili", CategoryType.EXPENSE), "Household Supplies");
        categoryMap.put(new CategoryTypeRecord("Stationery & tools", CategoryType.EXPENSE), "Household Supplies");
        categoryMap.put(new CategoryTypeRecord("Gifts", CategoryType.EXPENSE), "Gifts");
        categoryMap.put(new CategoryTypeRecord("Electronics & accessories", CategoryType.EXPENSE), "Electronics");
        categoryMap.put(new CategoryTypeRecord("Pets, animals", CategoryType.EXPENSE), "Pets");
        categoryMap.put(new CategoryTypeRecord("Animali domestici", CategoryType.EXPENSE), "Pets");
        categoryMap.put(new CategoryTypeRecord("Casa", CategoryType.EXPENSE), "Furnishings");
        categoryMap.put(new CategoryTypeRecord("Mobili", CategoryType.EXPENSE), "Furnishings");
//        categoryMap.put(new CategoryTypeRecord("Kids", CategoryType.EXPENSE), "Kids");
        categoryMap.put(new CategoryTypeRecord("Health and beauty", CategoryType.EXPENSE), "Personal Care & Beauty");
        categoryMap.put(new CategoryTypeRecord("Health & beauty", CategoryType.EXPENSE), "Personal Care & Beauty");
        categoryMap.put(new CategoryTypeRecord("Jewels, accessories", CategoryType.EXPENSE), "Clothing");
        categoryMap.put(new CategoryTypeRecord("Gioielli & Accessori", CategoryType.EXPENSE), "Clothing");
        categoryMap.put(new CategoryTypeRecord("Clothes & shoes", CategoryType.EXPENSE), "Clothing");


        // Housing
        categoryMap.put(new CategoryTypeRecord("Property insurance", CategoryType.EXPENSE), "Home Insurance");
        categoryMap.put(new CategoryTypeRecord("Insurance (Housing)", CategoryType.EXPENSE), "Home Insurance");
        categoryMap.put(new CategoryTypeRecord("Housing", CategoryType.EXPENSE), "Housing");
        categoryMap.put(new CategoryTypeRecord("Maintenance, repairs", CategoryType.EXPENSE), "Maintenance & Repairs");
//        categoryMap.put(new CategoryTypeRecord("Services", CategoryType.EXPENSE), "Housing");
        categoryMap.put(new CategoryTypeRecord("Energy, utilities", CategoryType.EXPENSE), "Utilities");
        categoryMap.put(new CategoryTypeRecord("Energy & utilities", CategoryType.EXPENSE), "Utilities");
        categoryMap.put(new CategoryTypeRecord("Mortgage", CategoryType.EXPENSE), "Mortgage");
        categoryMap.put(new CategoryTypeRecord("Rent", CategoryType.EXPENSE), "Rent");


        //Transportation
        categoryMap.put(new CategoryTypeRecord("Transportation", CategoryType.EXPENSE), "Transportation");
        categoryMap.put(new CategoryTypeRecord("Transportation - Others", CategoryType.EXPENSE), "Transportation");
        categoryMap.put(new CategoryTypeRecord("Business trips", CategoryType.EXPENSE), "Work");
        categoryMap.put(new CategoryTypeRecord("Long distance", CategoryType.EXPENSE), "Flights");
        categoryMap.put(new CategoryTypeRecord("Taxi", CategoryType.EXPENSE), "Taxi & Ride-sharing");
        categoryMap.put(new CategoryTypeRecord("Public transport", CategoryType.EXPENSE), "Public Transit");
        categoryMap.put(new CategoryTypeRecord("Insurance (Vehicle)", CategoryType.EXPENSE), "Travel Insurance");


        //Vehicle
        categoryMap.put(new CategoryTypeRecord("Vehicle", CategoryType.EXPENSE), "Car Payment");
        categoryMap.put(new CategoryTypeRecord("Veicoli", CategoryType.EXPENSE), "Car Payment");
        categoryMap.put(new CategoryTypeRecord("Toll", CategoryType.EXPENSE), "Tolls");
        categoryMap.put(new CategoryTypeRecord("Vehicle insurance", CategoryType.EXPENSE), "Vehicle Insurance");
        categoryMap.put(new CategoryTypeRecord("Rentals", CategoryType.EXPENSE), "Car Rental");
        categoryMap.put(new CategoryTypeRecord("Vehicle maintenance", CategoryType.EXPENSE), "Vehicle Maintenance");
        categoryMap.put(new CategoryTypeRecord("Lavaggio", CategoryType.EXPENSE), "Vehicle Maintenance");
        categoryMap.put(new CategoryTypeRecord("Parking", CategoryType.EXPENSE), "Parking");
        categoryMap.put(new CategoryTypeRecord("Fuel", CategoryType.EXPENSE), "Fuel");


        //Life & Entertainment
        categoryMap.put(new CategoryTypeRecord("Life & Entertainment", CategoryType.EXPENSE), "Entertainment");
        categoryMap.put(new CategoryTypeRecord("Lottery, gambling", CategoryType.EXPENSE), "Entertainment");
        categoryMap.put(new CategoryTypeRecord("Charity, gifts", CategoryType.EXPENSE), "Charity");
        categoryMap.put(new CategoryTypeRecord("Holidays, trips, hotels", CategoryType.EXPENSE), "Hotels & Accommodation");
        categoryMap.put(new CategoryTypeRecord("TV, Streaming", CategoryType.EXPENSE), "Streaming Services");
        categoryMap.put(new CategoryTypeRecord("Tv, streaming", CategoryType.EXPENSE), "Streaming Services");
        categoryMap.put(new CategoryTypeRecord("Books", CategoryType.EXPENSE), "Books");
        categoryMap.put(new CategoryTypeRecord("Education & development", CategoryType.EXPENSE), "Education");
        categoryMap.put(new CategoryTypeRecord("Hobbies", CategoryType.EXPENSE), "Hobbies");
        categoryMap.put(new CategoryTypeRecord("Tempo libero", CategoryType.EXPENSE), "Hobbies");
        categoryMap.put(new CategoryTypeRecord("Free time", CategoryType.EXPENSE), "Hobbies");
        categoryMap.put(new CategoryTypeRecord("Life events", CategoryType.EXPENSE), "Life Events");
        categoryMap.put(new CategoryTypeRecord("Eventi", CategoryType.EXPENSE), "Life Events");
        categoryMap.put(new CategoryTypeRecord("Culture, sport events", CategoryType.EXPENSE), "Movies & Events");
        categoryMap.put(new CategoryTypeRecord("Active sport, fitness", CategoryType.EXPENSE), "Fitness");
        categoryMap.put(new CategoryTypeRecord("Wellness, beauty", CategoryType.EXPENSE), "Personal Care & Beauty");
        categoryMap.put(new CategoryTypeRecord("Health care, doctor", CategoryType.EXPENSE), "Doctor & Medical");

        //Communication, PC
        categoryMap.put(new CategoryTypeRecord("Communication, PC", CategoryType.EXPENSE), "Communication, PC & Software");
        categoryMap.put(new CategoryTypeRecord("Postal services", CategoryType.EXPENSE), "Postal Services");
        categoryMap.put(new CategoryTypeRecord("App", CategoryType.EXPENSE), "Software");
        categoryMap.put(new CategoryTypeRecord("Software", CategoryType.EXPENSE), "Software");
        categoryMap.put(new CategoryTypeRecord("Giochi", CategoryType.EXPENSE), "Gaming");
        categoryMap.put(new CategoryTypeRecord("Internet", CategoryType.EXPENSE), "Internet & Phone");
        categoryMap.put(new CategoryTypeRecord("Telephony, mobile phone", CategoryType.EXPENSE), "Internet & Phone");
        categoryMap.put(new CategoryTypeRecord("Phone, cell phones", CategoryType.EXPENSE), "Internet & Phone");


        //Financial expenses
        categoryMap.put(new CategoryTypeRecord("Financial expenses", CategoryType.EXPENSE), "Financial Services");
//        categoryMap.put(new CategoryTypeRecord("Child Support", CategoryType.EXPENSE), "Financial Services");
        categoryMap.put(new CategoryTypeRecord("Charges, fees", CategoryType.EXPENSE), "Fees");
        categoryMap.put(new CategoryTypeRecord("Advisory", CategoryType.EXPENSE), "Financial Services");
        categoryMap.put(new CategoryTypeRecord("Fines", CategoryType.EXPENSE), "Fines");
        categoryMap.put(new CategoryTypeRecord("Loan, interests", CategoryType.EXPENSE), "Loan Repayment");
//        categoryMap.put(new CategoryTypeRecord("Insurances", CategoryType.EXPENSE), "Financial Services");
        categoryMap.put(new CategoryTypeRecord("Taxes", CategoryType.EXPENSE), "Taxes");


        //Investments
        categoryMap.put(new CategoryTypeRecord("Investments", CategoryType.EXPENSE), "Investments");
//        categoryMap.put(new CategoryTypeRecord("Collections", CategoryType.EXPENSE), "Investments");
//        categoryMap.put(new CategoryTypeRecord("Savings", CategoryType.EXPENSE), "Investments");
        categoryMap.put(new CategoryTypeRecord("Financial investments", CategoryType.EXPENSE), "Investments");
        categoryMap.put(new CategoryTypeRecord("Fin. investments", CategoryType.EXPENSE), "Investments");
//        categoryMap.put(new CategoryTypeRecord("Vehicles, chattels", CategoryType.EXPENSE), "Investments");
//        categoryMap.put(new CategoryTypeRecord("Realty", CategoryType.EXPENSE), "Investments");
        categoryMap.put(new CategoryTypeRecord("Insurance", CategoryType.EXPENSE), "Vehicle Insurance");


        //Others
        categoryMap.put(new CategoryTypeRecord("Others", CategoryType.EXPENSE), "Others");
        categoryMap.put(new CategoryTypeRecord("Missing", CategoryType.EXPENSE), "Missing");
        categoryMap.put(new CategoryTypeRecord("Splitwise", CategoryType.EXPENSE), "Splitwise");
        categoryMap.put(new CategoryTypeRecord("Matrimonio", CategoryType.EXPENSE), "Life Events");
        categoryMap.put(new CategoryTypeRecord("Income", CategoryType.EXPENSE), "Others");

        // Income mappings
        categoryMap.put(new CategoryTypeRecord("Income", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Gifts", CategoryType.INCOME), "Gifts");
//        categoryMap.put(new CategoryTypeRecord("Child Support", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Refunds (tax, purchase)", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Refunds", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Lottery, gambling", CategoryType.INCOME), "Income");
//        categoryMap.put(new CategoryTypeRecord("Checks, coupons", CategoryType.INCOME), "Income");
//        categoryMap.put(new CategoryTypeRecord("Lending, renting", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Dues & grants", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Dues, grants", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Rental income", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Rental, income", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Beni immobili", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Sale", CategoryType.INCOME), "Sale");
        categoryMap.put(new CategoryTypeRecord("Vendite", CategoryType.INCOME), "Sale");
        categoryMap.put(new CategoryTypeRecord("Interests, dividends", CategoryType.INCOME), "Investments");
        categoryMap.put(new CategoryTypeRecord("Wage, invoices", CategoryType.INCOME), "Salary");
        categoryMap.put(new CategoryTypeRecord("Unknown Income", CategoryType.INCOME), "Income");
        categoryMap.put(new CategoryTypeRecord("Missing", CategoryType.INCOME), "Income");

        //Refunds income
        categoryMap.put(new CategoryTypeRecord("Splitwise", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Others", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Fin. investments", CategoryType.INCOME), "Investments");
        categoryMap.put(new CategoryTypeRecord("Clothes & shoes", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Coffee & Drinks", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Restaurants", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Food & Dining", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Transportation - Others", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Software", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Groceries", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Electronics & accessories", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Books", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Energy, utilities", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Housing", CategoryType.INCOME), "Refunds");
        categoryMap.put(new CategoryTypeRecord("Casa", CategoryType.INCOME), "Refunds");


        //Transfer
        categoryMap.put(new CategoryTypeRecord("Transfer", CategoryType.INCOME), "Transfer");
        categoryMap.put(new CategoryTypeRecord("Transfer", CategoryType.EXPENSE), "Transfer");
    }

    List<String> labelToDrop = List.of("Games", "Dividere", "Investimenti");
    Map<String, String> labelToPayee = Map.of("Amazon", "Amazon", "Lavoro", "Work", "Work", "Work");

    @Mapping(target = "totalRecords", expression = "java(json.records() != null ? json.records().size() : 0)")
    public abstract WalletExportDto toDto(WalletExportJson json);


    @Mapping(target = "value", source = "amount.value")
    @Mapping(target = "category", source = ".", qualifiedByName = "mapCategoryName")
    @Mapping(target = "type", source = "recordType")
    @Mapping(target = "date", source = "recordDate")
    @Mapping(target = "labels", source = ".", qualifiedByName = "mapLabels")
    @Mapping(target = "payee", source = ".", qualifiedByName = "mapPayee")
    public abstract WalletExportDto.RecordDto toDto(WalletExportJson.RecordJson json);

    @Named("mapLabels")
    List<String> mapLabels(WalletExportJson.RecordJson record) {
        if (CollectionUtils.isEmpty(record.labels())) {
            return null;
        }

        return record.labels().stream().map(WalletExportJson.LabelJson::name)
                .filter(label -> !labelToDrop.contains(label))
                .filter(label -> !labelToPayee.containsKey(label))
                .toList();
    }

    @Named("mapPayee")
    String mapPayee(WalletExportJson.RecordJson record) {
        if (StringUtils.isNotEmpty(record.payee())) {
            return record.payee();
        }

        if (CollectionUtils.isNotEmpty(record.labels())) {
            for (WalletExportJson.LabelJson label : record.labels()) {
                if (labelToPayee.containsKey(label.name())) {
                    return labelToPayee.get(label.name());
                }
            }
        }
        return null;
    }

    @Named("mapCategoryName")
    String mapCategoryName(WalletExportJson.RecordJson record) {
        CategoryType categoryType = CategoryType.valueOf(record.recordType().toUpperCase());
        CategoryTypeRecord categoryTypeRecord = new CategoryTypeRecord(record.category().name(), categoryType);

        return categoryMap.containsKey(categoryTypeRecord) ? categoryMap.get(categoryTypeRecord) : returnEmpty(categoryTypeRecord, record);
    }

    private String returnEmpty(CategoryTypeRecord categoryTypeRecord, WalletExportJson.RecordJson record) {
        log.info("Missing category: {}, for record {}", categoryTypeRecord, record.id());
        return StringUtils.EMPTY;
    }


    private record CategoryTypeRecord(String name, CategoryType type) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CategoryTypeRecord that)) return false;
            return type == that.type && name.equalsIgnoreCase(that.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name != null ? name.toLowerCase() : null, type);
        }
    }
}
