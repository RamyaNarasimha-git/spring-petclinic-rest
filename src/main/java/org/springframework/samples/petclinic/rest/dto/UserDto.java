package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An user.
 */

@Schema(name = "User", description = "An user.")
@JsonTypeName("User")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T14:07:02.895195200+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public class UserDto {

  private String username;

  private @Nullable String password;

  private @Nullable Boolean enabled;

  private List<@Valid RoleDto> roles = new ArrayList<>();

  public UserDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserDto(String username) {
    this.username = username;
  }

  public UserDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * The username
   * @return username
   */
  @NotNull @Size(min = 1, max = 80)
  @Schema(name = "username", example = "john.doe", description = "The username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  @JsonProperty("username")
  public void setUsername(String username) {
    this.username = username;
  }

  public UserDto password(@Nullable String password) {
    this.password = password;
    return this;
  }

  /**
   * The password
   * @return password
   */
  @Size(min = 1, max = 80)
  @Schema(name = "password", example = "1234abc", description = "The password", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("password")
  public @Nullable String getPassword() {
    return password;
  }

  @JsonProperty("password")
  public void setPassword(@Nullable String password) {
    this.password = password;
  }

  public UserDto enabled(@Nullable Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Indicates if the user is enabled
   * @return enabled
   */

  @Schema(name = "enabled", example = "true", description = "Indicates if the user is enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabled")
  public @Nullable Boolean getEnabled() {
    return enabled;
  }

  @JsonProperty("enabled")
  public void setEnabled(@Nullable Boolean enabled) {
    this.enabled = enabled;
  }

  public UserDto roles(List<@Valid RoleDto> roles) {
    this.roles = roles;
    return this;
  }

  public UserDto addRolesItem(RoleDto rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * The roles of an user
   * @return roles
   */
  @Valid
  @Schema(name = "roles", description = "The roles of an user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<@Valid RoleDto> getRoles() {
    return roles;
  }

  @JsonProperty("roles")
  public void setRoles(List<@Valid RoleDto> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDto user = (UserDto) o;
    return Objects.equals(this.username, user.username) &&
        Objects.equals(this.password, user.password) &&
        Objects.equals(this.enabled, user.enabled) &&
        Objects.equals(this.roles, user.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, enabled, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDto {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

