package astoppello.wallet.bootstrap.walletexport;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface WalletExportMapper {

    WalletExportDto toDto(WalletExportJson json);


    @Mapping(target = "value", source = "amount.value")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "type", source = "recordType")
    @Mapping(target = "date", source = "recordDate")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "listToLabels")
    WalletExportDto.RecordDto toDto(WalletExportJson.RecordJson json);

    @Named("listToLabels")
    default List<String> listToLabels(List<WalletExportJson.LabelJson> labels) {
        if(labels == null) { return null;}
        return labels.stream().map(WalletExportJson.LabelJson::name).toList();
    }
}
